package com.example.Taller_Tienda.Model;

import jakarta.persistence.*;

@Entity @Table(name="order_items")
public class OrderItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="order_id", nullable=false)
  private Order order;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false)
  private Product product;

  private int qty;

  @Column(name="price_cents", nullable=false)
  private Long priceCents;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }
  public int getQty() { return qty; }
  public void setQty(int qty) { this.qty = qty; }
  public Long getPriceCents() { return priceCents; }
  public void setPriceCents(Long priceCents) { this.priceCents = priceCents; }
}
