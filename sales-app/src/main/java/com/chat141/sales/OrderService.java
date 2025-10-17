package com.chat141.sales;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class OrderService {

  @PersistenceContext EntityManager em;

  @Resource(lookup = "java:/jms/queue/orderEvents")
  Queue orderEvents;

  @Inject JMSContext jms;

  @Transactional
  public Long create(OrderDTO dto) {
    Customer c = em.find(Customer.class, dto.customerId);
    if (c == null) throw new IllegalArgumentException("Customer not found: " + dto.customerId);
    Order o = new Order();
    o.setCustomer(c);

    BigDecimal total = BigDecimal.ZERO;
    for (OrderDTO.Item it : dto.items) {
      Product p = em.find(Product.class, it.productId);
      if (p == null) throw new IllegalArgumentException("Product not found: " + it.productId);
      OrderItem oi = new OrderItem();
      oi.setOrder(o);
      oi.setProduct(p);
      oi.setQty(it.qty);
      oi.setUnitPrice(it.unitPrice != null ? it.unitPrice : p.getPrice());
      o.getItems().add(oi);
      total = total.add(oi.getUnitPrice().multiply(java.math.BigDecimal.valueOf(oi.getQty())));
    }
    o.setTotal(total);
    em.persist(o);
    jms.createProducer().send(orderEvents, new OrderEvent("OrderCreated", o.getId()));
    return o.getId();
  }

  @Transactional
  public void markPaid(Long id) {
    Order o = em.find(Order.class, id);
    if (o == null) throw new IllegalArgumentException("Order not found: " + id);
    o.setStatus(Order.Status.PAID);
    jms.createProducer().send(orderEvents, new OrderEvent("OrderPaid", o.getId()));
  }
}
