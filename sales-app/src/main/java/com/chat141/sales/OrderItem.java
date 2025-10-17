package com.chat141.sales;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class OrderItem implements Serializable {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Order order;

  @ManyToOne(optional = false)
  private Product product;

  private int qty;
  private BigDecimal unitPrice;

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
  public int getQty() { return qty; }
  public void setQty(int qty) { this.qty = qty; }
  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
