package com.chat141.sales;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class Product implements Serializable {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false) private String sku;
  private String name;
  private BigDecimal price;

  @ManyToOne(optional = false)
  private Supplier supplier;

  // getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public Supplier getSupplier() { return supplier; }
  public void setSupplier(Supplier supplier) { this.supplier = supplier; }
}
