package com.example.Taller_Tienda.Repository.order;

import com.example.Taller_Tienda.Model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
