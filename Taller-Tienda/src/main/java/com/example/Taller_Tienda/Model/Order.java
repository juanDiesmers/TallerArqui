package com.example.Taller_Tienda.Model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
public class Order {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
  private User user;

  @Column(length=20, nullable=false, unique=true)
  private String code;

  @Enumerated(EnumType.STRING) @Column(nullable=false)
  private Status status;

  @Column(name="total_cents", nullable=false)
  private Long totalCents;

  @Column(name="created_at", updatable=false)
  private Instant createdAt = Instant.now();

  @Column(name="updated_at")
  private Instant updatedAt = Instant.now();

  @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<OrderItem> items = new ArrayList<>();

  public enum Status { NEW, PAID, SHIPPED, CLOSED, CANCELLED }

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public Long getTotalCents() { return totalCents; }
  public void setTotalCents(Long totalCents) { this.totalCents = totalCents; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
}
