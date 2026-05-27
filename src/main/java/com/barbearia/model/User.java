package com.barbearia.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // CLIENTE, BARBEIRO, DONO

    private String resetToken;
    private Instant resetExpiresAt;

    @Column(name = "customer_id")
    private Long customerId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public Instant getResetExpiresAt() { return resetExpiresAt; }
    public void setResetExpiresAt(Instant resetExpiresAt) { this.resetExpiresAt = resetExpiresAt; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
