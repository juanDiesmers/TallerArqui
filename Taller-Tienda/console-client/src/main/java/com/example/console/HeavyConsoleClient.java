package com.example.console;

import java.util.Scanner;
import java.util.concurrent.*;

public class HeavyConsoleClient {

  private static final String REST_BASE = "http://localhost:8080/api";
  private static final String SOAP_URL = "http://localhost:8080/ws";

  private final RestClient restClient;
  private final SoapClient soapClient;
  private final RpcClient rpcClient;
  private final ExecutorService executor;

  public HeavyConsoleClient() {
    this.restClient = new RestClient(REST_BASE);
    this.soapClient = new SoapClient(SOAP_URL);
    this.rpcClient = new RpcClient(); // gRPC o custom RPC
    this.executor = Executors.newFixedThreadPool(10);
  }

  public static void main(String[] args) {
    HeavyConsoleClient client = new HeavyConsoleClient();
    client.run();
  }

  public void run() {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    System.out.println("╔══════════════════════════════════════════╗");
    System.out.println("║   CLIENTE PESADO - TALLER TIENDA        ║");
    System.out.println("╚══════════════════════════════════════════╝");

    while (running) {
      printMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> testRest();
        case "2" -> testSoap();
        case "3" -> testRpc();
        case "4" -> heavyLoadRest();
        case "5" -> heavyLoadSoap();
        case "6" -> heavyLoadMixed();
        case "7" -> comparePerformance();
        case "0" -> {
          running = false;
          shutdown();
        }
        default -> System.out.println("❌ Opción inválida");
      }
    }

    scanner.close();
  }

  private void printMenu() {
    System.out.println("\n┌─────────────────────────────────────────┐");
    System.out.println("│ MENÚ PRINCIPAL                          │");
    System.out.println("├─────────────────────────────────────────┤");
    System.out.println("│ 1. Probar REST (listar productos)       │");
    System.out.println("│ 2. Probar SOAP (consultar producto)     │");
    System.out.println("│ 3. Probar RPC (operaciones batch)       │");
    System.out.println("│ 4. Carga pesada REST (100 req/s)        │");
    System.out.println("│ 5. Carga pesada SOAP (50 req/s)         │");
    System.out.println("│ 6. Carga pesada MIXTA (REST+SOAP+RPC)   │");
    System.out.println("│ 7. Comparar rendimiento                 │");
    System.out.println("│ 0. Salir                                │");
    System.out.println("└─────────────────────────────────────────┘");
    System.out.print("Selecciona una opción: ");
  }

  // ========== PRUEBAS INDIVIDUALES ==========

  private void testRest() {
    System.out.println("\n🔵 Probando REST...");
    try {
      var products = restClient.getAllProducts();
      System.out.println("✅ Productos encontrados: " + products.size());
      products
        .stream()
        .limit(3)
        .forEach(p ->
          System.out.printf(
            "   - [%d] %s - $%s (stock: %d)%n",
            p.id,
            p.nombre,
            p.precio,
            p.stock
          )
        );
    } catch (Exception e) {
      System.err.println("❌ Error REST: " + e.getMessage());
    }
  }

  private void testSoap() {
    System.out.println("\n🟢 Probando SOAP...");
    try {
      var producto = soapClient.getProducto(1L);
      System.out.printf(
        "✅ Producto obtenido: [%d] %s - $%s%n",
        producto.getId(),
        producto.getNombre(),
        producto.getPrecio()
      );
    } catch (Exception e) {
      System.err.println("❌ Error SOAP: " + e.getMessage());
    }
  }

  private void testRpc() {
    System.out.println("\n🟣 Probando RPC...");
    try {
      var result = rpcClient.batchReserve(
        new long[] { 1, 2, 3 },
        new int[] { 1, 2, 1 }
      );
      System.out.println("✅ Reservas completadas: " + result.success);
    } catch (Exception e) {
      System.err.println("❌ Error RPC: " + e.getMessage());
    }
  }

  // ========== CARGAS PESADAS ==========

  private void heavyLoadRest() {
    System.out.println("\n⚡ CARGA PESADA REST (100 req/s durante 30s)");
    System.out.println("Presiona ENTER para iniciar...");
    new Scanner(System.in).nextLine();

    int targetQps = 100;
    int durationSeconds = 30;
    long periodMs = 1000 / targetQps;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    Semaphore semaphore = new Semaphore(20); // max concurrencia

    var stats = new Stats();

    scheduler.scheduleAtFixedRate(
      () -> {
        if (semaphore.tryAcquire()) {
          executor.submit(() -> {
            try {
              long start = System.nanoTime();
              restClient.getAllProducts();
              long elapsed = System.nanoTime() - start;
              stats.record(elapsed / 1_000_000); // ms
            } catch (Exception e) {
              stats.recordError();
            } finally {
              semaphore.release();
            }
          });
        }
      },
      0,
      periodMs,
      TimeUnit.MILLISECONDS
    );

    try {
      Thread.sleep(durationSeconds * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    scheduler.shutdown();
    stats.print("REST");
  }

  private void heavyLoadSoap() {
    System.out.println("\n⚡ CARGA PESADA SOAP (50 req/s durante 30s)");
    System.out.println("Presiona ENTER para iniciar...");
    new Scanner(System.in).nextLine();

    int targetQps = 50;
    int durationSeconds = 30;
    long periodMs = 1000 / targetQps;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    Semaphore semaphore = new Semaphore(15);

    var stats = new Stats();
    var random = new java.util.Random();

    scheduler.scheduleAtFixedRate(
      () -> {
        if (semaphore.tryAcquire()) {
          executor.submit(() -> {
            try {
              long start = System.nanoTime();
              long id = 1 + random.nextInt(5); // productos 1-5
              soapClient.getProducto(id);
              long elapsed = System.nanoTime() - start;
              stats.record(elapsed / 1_000_000);
            } catch (Exception e) {
              stats.recordError();
            } finally {
              semaphore.release();
            }
          });
        }
      },
      0,
      periodMs,
      TimeUnit.MILLISECONDS
    );

    try {
      Thread.sleep(durationSeconds * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    scheduler.shutdown();
    stats.print("SOAP");
  }

  private void heavyLoadMixed() {
    System.out.println("\n⚡ CARGA PESADA MIXTA");
    System.out.println("   - REST: 60 req/s");
    System.out.println("   - SOAP: 30 req/s");
    System.out.println("   - RPC:  10 req/s");
    System.out.println("   Duración: 30 segundos");
    System.out.println("\nPresiona ENTER para iniciar...");
    new Scanner(System.in).nextLine();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(6);

    var restStats = new Stats();
    var soapStats = new Stats();
    var rpcStats = new Stats();

    int duration = 30;

    // REST: 60/s
    scheduler.scheduleAtFixedRate(
      () -> {
        executor.submit(() -> {
          try {
            long start = System.nanoTime();
            restClient.getAllProducts();
            restStats.record((System.nanoTime() - start) / 1_000_000);
          } catch (Exception e) {
            restStats.recordError();
          }
        });
      },
      0,
      1000 / 60,
      TimeUnit.MILLISECONDS
    );

    // SOAP: 30/s
    scheduler.scheduleAtFixedRate(
      () -> {
        executor.submit(() -> {
          try {
            long start = System.nanoTime();
            soapClient.getProducto(1L + new java.util.Random().nextInt(5));
            soapStats.record((System.nanoTime() - start) / 1_000_000);
          } catch (Exception e) {
            soapStats.recordError();
          }
        });
      },
      0,
      1000 / 30,
      TimeUnit.MILLISECONDS
    );

    // RPC: 10/s
    scheduler.scheduleAtFixedRate(
      () -> {
        executor.submit(() -> {
          try {
            long start = System.nanoTime();
            rpcClient.batchReserve(new long[] { 1, 2 }, new int[] { 1, 1 });
            rpcStats.record((System.nanoTime() - start) / 1_000_000);
          } catch (Exception e) {
            rpcStats.recordError();
          }
        });
      },
      0,
      1000 / 10,
      TimeUnit.MILLISECONDS
    );

    try {
      Thread.sleep(duration * 1000L);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    scheduler.shutdown();

    System.out.println("\n📊 RESULTADOS CARGA MIXTA:");
    restStats.print("REST");
    soapStats.print("SOAP");
    rpcStats.print("RPC");
  }

  private void comparePerformance() {
    System.out.println("\n📊 COMPARACIÓN DE RENDIMIENTO");
    System.out.println("Ejecutando 100 peticiones de cada tipo...\n");

    int iterations = 100;

    // Test REST
    var restStats = new Stats();
    for (int i = 0; i < iterations; i++) {
      try {
        long start = System.nanoTime();
        restClient.getAllProducts();
        restStats.record((System.nanoTime() - start) / 1_000_000);
      } catch (Exception e) {
        restStats.recordError();
      }
    }

    // Test SOAP
    var soapStats = new Stats();
    for (int i = 0; i < iterations; i++) {
      try {
        long start = System.nanoTime();
        soapClient.getProducto(1L);
        soapStats.record((System.nanoTime() - start) / 1_000_000);
      } catch (Exception e) {
        soapStats.recordError();
      }
    }

    System.out.println("═══════════════════════════════════════════");
    restStats.print("REST");
    soapStats.print("SOAP");
    System.out.println("═══════════════════════════════════════════");
  }

  private void shutdown() {
    System.out.println("\n👋 Cerrando cliente...");
    executor.shutdown();
    try {
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
    System.out.println("✅ Cliente cerrado correctamente");
  }

  // ========== CLASE STATS ==========

  static class Stats {

    private long total = 0;
    private long errors = 0;
    private long sum = 0;
    private long min = Long.MAX_VALUE;
    private long max = 0;

    synchronized void record(long latencyMs) {
      total++;
      sum += latencyMs;
      min = Math.min(min, latencyMs);
      max = Math.max(max, latencyMs);
    }

    synchronized void recordError() {
      errors++;
    }

    void print(String protocol) {
      long success = total - errors;
      double avg = success > 0 ? (double) sum / success : 0;

      System.out.printf("\n┌─ %s ─────────────────────────────┐%n", protocol);
      System.out.printf("│ Total requests:  %,10d      │%n", total + errors);
      System.out.printf("│ Successful:      %,10d      │%n", success);
      System.out.printf("│ Errors:          %,10d      │%n", errors);
      System.out.printf(
        "│ Success rate:    %9.2f%%     │%n",
        (total + errors > 0 ? 100.0 * success / (total + errors) : 0)
      );
      System.out.printf("│ Avg latency:     %9.2f ms   │%n", avg);
      System.out.printf(
        "│ Min latency:     %,10d ms   │%n",
        min == Long.MAX_VALUE ? 0 : min
      );
      System.out.printf("│ Max latency:     %,10d ms   │%n", max);
      System.out.println("└────────────────────────────────────────┘");
    }
  }
}
