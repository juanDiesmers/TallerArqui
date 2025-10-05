package com.example.Taller_Tienda.Controller;

import com.example.Taller_Tienda.Model.Order;
import com.example.Taller_Tienda.Repository.order.OrderRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/orders")
public class OrderController {
  private final OrderRepository repo;
  public OrderController(OrderRepository repo) { this.repo = repo; }

  @GetMapping
  public Page<Order> recent(@PathVariable Long userId,
                            @RequestParam(defaultValue="0") int page,
                            @RequestParam(defaultValue="50") int size) {
    return repo.findRecentByUser(userId, PageRequest.of(page, size));
  }
}