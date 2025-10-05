package com.example.Taller_Tienda.Repository.order;

import com.example.Taller_Tienda.Model.Order;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @Query("SELECT o FROM Order o WHERE o.user.id=:userId ORDER BY o.createdAt DESC")
  Page<Order> findRecentByUser(Long userId, Pageable pageable);
}
