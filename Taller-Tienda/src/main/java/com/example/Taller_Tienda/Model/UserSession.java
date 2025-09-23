package com.example.Taller_Tienda.Model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="user_sessions")
public class UserSession {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
  private User user;

  @Column(name="login_at", nullable=false)
  private Instant loginAt = Instant.now();

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }
  public Instant getLoginAt() { return loginAt; }
  public void setLoginAt(Instant loginAt) { this.loginAt = loginAt; }
}
