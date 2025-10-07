package com.example.Taller_Tienda.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import com.example.Taller_Tienda.Model.Order;
import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Model.User;
import com.example.Taller_Tienda.Repository.inventario.ProductRepository;
import com.example.Taller_Tienda.Repository.order.OrderRepository;
import com.example.Taller_Tienda.Repository.user.UserRepository;

@Configuration
@Profile("heavy")
@EnableJpaRepositories(
        basePackages = "com.example.Taller_Tienda.Repository.order",
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class HeavyClientRunner implements ApplicationRunner {

  private final UserRepository userRepo;
  private final OrderRepository orderRepo;
  private final ProductService productService;
  private final ProductRepository productRepo;

  
  @Autowired
  @Qualifier("orderDataSource")
  private  DataSource dataSource;

  @Value("${heavy.startup.wait-ms:15000}")
  private long waitMs;

  @Value("${heavy.qps.writes:50}")
  private int writesQps;
  @Value("${heavy.qps.reads:120}")
  private int readsQps;
  @Value("${heavy.qps.reservations:40}")
  private int reserveQps;

  // —— NUEVOS knobs de restock —— //
  @Value("${heavy.qps.restock:10}")
  private int restockQps;               // operaciones de restock por segundo
  @Value("${heavy.restock.threshold:10}")
  private int restockThreshold;         // si stock < threshold => reponer
  @Value("${heavy.restock.amount:50}")
  private int restockAmount;            // cuánto sumar por operación
  @Value("${heavy.restock.batch-size:3}")
  private int restockBatchSize;         // cuántos productos “bajo stock” revisar por tick
  @Value("${heavy.restock.cap-enabled:false}")
  private boolean restockCapEnabled;    // true para usar un tope máximo
  @Value("${heavy.restock.cap:200}")
  private int restockCap;               // tope máximo por producto

  @Value("${heavy.max.concurrency:16}")
  private int maxConcurrency;

  public HeavyClientRunner(UserRepository userRepo,
                           OrderRepository orderRepo,
                           ProductService productService,
                           ProductRepository productRepo
                           ) {
    this.userRepo = userRepo;
    this.orderRepo = orderRepo;
    this.productService = productService;
    this.productRepo = productRepo;
  }

  @Override
  public void run(org.springframework.boot.ApplicationArguments args) {
    waitForTablesOrTimeout();

    seedProductsIfEmpty();

    var user = userRepo.findByEmail("pesado@example.com")
        .orElseGet(this::buildAndSaveUser);

    var pool = Executors.newFixedThreadPool(Math.max(maxConcurrency,
        Runtime.getRuntime().availableProcessors() * 2));
    var writesSem = new Semaphore(maxConcurrency);
    var readsSem  = new Semaphore(maxConcurrency);
    var resSem    = new Semaphore(maxConcurrency);
    var restSem   = new Semaphore(maxConcurrency); // 👈 para restock

    var scheduler = Executors.newScheduledThreadPool(4);

    long writePeriodMs = Math.max(1, 1000L / Math.max(1, writesQps));
    long readPeriodMs  = Math.max(1, 1000L / Math.max(1, readsQps));
    long resPeriodMs   = Math.max(1, 1000L / Math.max(1, reserveQps));
    long restPeriodMs  = Math.max(1, 1000L / Math.max(1, restockQps)); // 👈

    System.out.printf(
        "[heavy] Iniciando carga continua: writes=%d/s, reads=%d/s, reserves=%d/s, restock=%d/s, maxConc=%d (th=%d, amt=%d, cap=%s/%d)%n",
        writesQps, readsQps, reserveQps, restockQps, maxConcurrency,
        restockThreshold, restockAmount, restockCapEnabled, restockCap
    );

    // Escrituras
    scheduler.scheduleWithFixedDelay(() -> {
      if (writesSem.tryAcquire()) {
        pool.submit(() -> {
          try { createOrder(user); } catch (Exception ignored) {}
          finally { writesSem.release(); }
        });
      }
    }, 0, writePeriodMs, TimeUnit.MILLISECONDS);

    // Lecturas
    scheduler.scheduleWithFixedDelay(() -> {
      if (readsSem.tryAcquire()) {
        pool.submit(() -> {
          try { readRecent(user.getId()); } catch (Exception ignored) {}
          finally { readsSem.release(); }
        });
      }
    }, 0, readPeriodMs, TimeUnit.MILLISECONDS);

    // Reservas (decrementa stock)
    scheduler.scheduleWithFixedDelay(() -> {
      if (resSem.tryAcquire()) {
        pool.submit(() -> {
          try { reserveRandom(); } catch (Exception ignored) {}
          finally { resSem.release(); }
        });
      }
    }, 0, resPeriodMs, TimeUnit.MILLISECONDS);

    // —— NUEVO: Restock (incrementa stock cuando está bajo) —— //
    scheduler.scheduleWithFixedDelay(() -> {
      if (restSem.tryAcquire()) {
        pool.submit(() -> {
          try { restockLowProductsTick(); } catch (Exception ignored) {}
          finally { restSem.release(); }
        });
      }
    }, 0, restPeriodMs, TimeUnit.MILLISECONDS);
  }

  // ——— Restock tick ———
  private void restockLowProductsTick() {
    // Busca algunos productos por debajo del umbral
    var lows = productService.findLowStock(restockThreshold, Math.max(1, restockBatchSize));
    if (lows.isEmpty()) return;

    for (var p : lows) {
      int updated = (restockCapEnabled)
          ? productService.restockCapped(p.getId(), restockAmount, restockCap)
          : productService.restock(p.getId(), restockAmount);

      if (updated > 0) {
        System.out.printf("[heavy][restock] id=%d +%d (cap=%s)%n",
            p.getId(), restockAmount, restockCapEnabled ? restockCap : null);
      }
    }
  }

  // ===== siembra productos =====
  private void seedProductsIfEmpty() {
    try {
      long c = productRepo.count();
      long total = productRepo.findAll().stream().mapToLong(Product::getStock).sum();
      System.out.println("[heavy] Productos actuales: " + c + ", stock total: " + total);
      if (c > 0) return;

      var items = List.of(
          build("Teclado Mecánico", "Switches azules",   "199000", 50),
          build("Mouse Inalámbrico","2.4G + BT",         "85000",  120),
          build("Monitor 24\"",     "IPS 75Hz",          "650000", 70),
          build("Webcam HD",        "1080p",             "120000", 90),
          build("Headset Gamer",    "7.1 Surround",      "230000", 60)
      );
      productRepo.saveAll(items);
      try { productRepo.flush(); } catch (Exception ignored) {}
      System.out.println("[heavy] Productos sembrados. Total ahora: " + productRepo.count());
    } catch (Exception e) {
      //e.printStackTrace();
      System.out.println("[heavy] No se pudieron sembrar productos: " + e.getMessage());
    }
  }

  private Product build(String nombre, String desc, String precio, int stock) {
    var p = new Product();
    p.setNombre(nombre);
    p.setDescripcion(desc);
    p.setPrecio(new BigDecimal(precio));
    p.setStock(stock);
    return p;
  }

  // ===== infra =====
  private void waitForTablesOrTimeout() {
    long deadline = System.currentTimeMillis() + waitMs;
    String[] needed = new String[]{"users","orders","order_items","user_sessions","producto"};
    while (System.currentTimeMillis() < deadline) {
      if (tablesExist(needed)) {
        System.out.println("[heavy] Tablas detectadas. Iniciando cliente pesado…");
        return;
      }
      System.out.println("[heavy] Esperando tablas… " + String.join(", ", needed));
      try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException ignored) {}
    }
    System.out.println("[heavy] Timeout de espera alcanzado. Continuando de todos modos.");
  }

  private boolean tablesExist(String[] tableNames) {
    final String sql =
        "SELECT COUNT(*) AS c FROM information_schema.tables " +
        "WHERE table_schema = DATABASE() AND table_name IN (" +
        String.join(",", java.util.Collections.nCopies(tableNames.length, "?")) + ")";
    try (Connection cn = dataSource.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
      for (int i = 0; i < tableNames.length; i++) ps.setString(i + 1, tableNames[i]);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getInt("c") == tableNames.length;
      }
    } catch (Exception e) {
      return false;
    }
  }

  private User buildAndSaveUser() {
    var u = new User();
    u.setEmail("pesado@example.com");
    u.setPasswordHash(new byte[]{1,2,3});
    u.setStatus(User.Status.ACTIVE);
    u.setCreatedAt(Instant.now());
    u.setUpdatedAt(Instant.now());
    return userRepo.save(u);
  }

  // ===== operaciones =====
  @Transactional
  void createOrder(User user) {
    var rnd = new SecureRandom();
    var o = new Order();
    o.setUser(user);
    o.setCode(UUID.randomUUID().toString().replace("-", "").substring(0,20));
    o.setStatus(Order.Status.values()[rnd.nextInt(Order.Status.values().length)]);
    o.setTotalCents(10_000L + rnd.nextInt(90_000));
    o.setCreatedAt(Instant.now());
    o.setUpdatedAt(Instant.now());
    orderRepo.save(o);
  }

  void readRecent(Long userId) {
    orderRepo.findRecentByUser(userId, PageRequest.of(0, 50)).getContent();
  }

  void reserveRandom() {
    productService.findRandom().ifPresent(p -> productService.reserveStock(p.getId(), 1));
  }
}
