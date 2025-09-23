package com.example.Taller_Tienda.Model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true)
  private String email;

  @Column(name="password_hash", nullable=false)
  private byte[] passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private Status status = Status.ACTIVE;

  @Column(name="created_at", updatable=false)
  private Instant createdAt = Instant.now();

  @Column(name="updated_at")
  private Instant updatedAt = Instant.now();

  public enum Status { ACTIVE, BLOCKED, DELETED }

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public byte[] getPasswordHash() { return passwordHash; }
  public void setPasswordHash(byte[] passwordHash) { this.passwordHash = passwordHash; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
