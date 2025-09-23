package com.example.Taller_Tienda.Repository;

import com.example.Taller_Tienda.Model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // Listado estable (lo más nuevo primero)
  List<Product> findAllByOrderByIdDesc();

  // Producto aleatorio (MySQL)
  @Query(value = "SELECT * FROM producto ORDER BY RAND() LIMIT 1", nativeQuery = true)
  Optional<Product> pickRandom();

  // Reserva (decrementa) stock de forma atómica si hay disponibilidad
  @Modifying
  @Query(value = "UPDATE producto SET stock = stock - :qty WHERE id = :id AND stock >= :qty", nativeQuery = true)
  int tryReserve(@Param("id") Long id, @Param("qty") int qty);

  // Suma total de stock (útil para métricas)
  @Query(value = "SELECT COALESCE(SUM(stock),0) FROM producto", nativeQuery = true)
  long sumStock();

  // Rellenar productos agotados en bloque (opcional)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(value = "UPDATE producto SET stock = stock + :add WHERE stock = 0", nativeQuery = true)
  int topUpZeroStock(@Param("add") int add);

  // ====== Métodos para reposición (restock) ======

  /**
   * Devuelve productos con stock por debajo de un umbral, ordenados por menor stock y luego ID.
   * Usa Pageable para limitar resultados de forma portable (evita problemas con "LIMIT :limit").
   */
  @Query(value = "SELECT * FROM producto WHERE stock < :threshold ORDER BY stock ASC, id ASC",
         nativeQuery = true)
  List<Product> pickLowStock(@Param("threshold") int threshold, int limit);

  /** Aumenta el stock en 'add' para el producto 'id'. Devuelve filas afectadas (0 o 1). */
  @Modifying
  @Query(value = "UPDATE producto SET stock = stock + :add WHERE id = :id", nativeQuery = true)
  int addStock(@Param("id") Long id, @Param("add") int add);

  /**
   * Aumenta stock respetando un tope máximo 'cap': stock = LEAST(stock + :add, :cap).
   * Sólo actualiza si el stock actual es menor al tope.
   */
  @Modifying
  @Query(value = "UPDATE producto SET stock = LEAST(stock + :add, :cap) " +
                 "WHERE id = :id AND stock < :cap", nativeQuery = true)
  int addStockCapped(@Param("id") Long id, @Param("add") int add, @Param("cap") int cap);
}
