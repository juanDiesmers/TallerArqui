package com.example.Taller_Tienda.Service;

import com.example.Taller_Tienda.Model.Product;
import com.example.Taller_Tienda.Repository.inventario.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository repo;

  public ProductService(ProductRepository repo) {
    this.repo = repo;
  }

  // ========= Usado por el Controller =========

  /** Lista productos en orden estable (más nuevos primero). */
  public List<Product> findAll() {
    return repo.findAllByOrderByIdDesc();
  }

  public Optional<Product> findById(Long id) {
    return repo.findById(id);
  }

  @Transactional
  public Product save(Product p) {
    // Si la entidad usa @CreationTimestamp en fechaCreacion, no necesitas setearla aquí.
    return repo.save(p);
  }

  @Transactional
  public void delete(Long id) {
    repo.deleteById(id);
  }

  // ========= Usado por HeavyClientRunner =========

  /** Devuelve un producto aleatorio (si hay). */
  public Optional<Product> findRandom() {
    return repo.pickRandom();
  }

  /** Intenta reservar (disminuir) stock de forma atómica. */
  @Transactional
  public boolean reserveStock(Long id, int qty) {
    if (qty <= 0) return false;
    return repo.tryReserve(id, qty) == 1;
  }

  // ========= Soporte de reabastecimiento (restock) =========

  /** Encuentra productos con stock por debajo del umbral (máximo 'limit' resultados). */
  public List<Product> findLowStock(int threshold, int limit) {
    if (limit <= 0) return List.of();
    return repo.pickLowStock(threshold, limit);
  }

  /** Aumenta el stock en 'qty' para el producto dado. */
  @Transactional
  public int restock(Long id, int qty) {
    if (qty <= 0) return 0;
    return repo.addStock(id, qty);
  }

  /** Aumenta el stock en 'qty' para el producto dado, respetando un tope máximo 'cap'. */
  @Transactional
  public int restockCapped(Long id, int qty, int cap) {
    if (qty <= 0 || cap <= 0) return 0;
    return repo.addStockCapped(id, qty, cap);
  }
}
