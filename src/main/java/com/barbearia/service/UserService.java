package com.barbearia.service;

import com.barbearia.domain.model.Customer;
import com.barbearia.domain.repository.CustomerRepository;
import com.barbearia.model.User;
import com.barbearia.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repo;
  private final CustomerRepository customerRepo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Value("${security.jwt.expirationMs:3600000}")
  private long jwtExpiration;

  public UserService(UserRepository repo, CustomerRepository customerRepo) {
    this.repo = repo;
    this.customerRepo = customerRepo;
  }

  public User register(String username, String email, String password, String role) {
    User u = new User();
    u.setUsername(username);
    u.setEmail(email);
    u.setPasswordHash(encoder.encode(password));
    String resolvedRole = role == null ? "CLIENTE" : role;
    u.setRole(resolvedRole);

    // If registering a CLIENTE, create a Customer record and link it
    if ("CLIENTE".equalsIgnoreCase(resolvedRole)) {
      Customer c = new Customer(username, email == null ? "" : email, "");
      Customer saved = customerRepo.save(c);
      u.setCustomerId(saved.getId());
    }

    return repo.save(u);
  }

  public Optional<User> findByUsername(String username) {
    return repo.findByUsername(username);
  }

  public Optional<User> findByEmail(String email) {
    return repo.findByEmail(email);
  }

  public Optional<User> findById(Long id) {
    return repo.findById(id);
  }

  public List<User> findAll() {
    return repo.findAll();
  }

  public boolean checkPassword(User user, String rawPassword) {
    return encoder.matches(rawPassword, user.getPasswordHash());
  }

  public void updatePassword(User user, String newPassword) {
    user.setPasswordHash(encoder.encode(newPassword));
    repo.save(user);
  }

  public void clearResetToken(User user) {
    user.setResetToken(null);
    user.setResetExpiresAt(null);
    repo.save(user);
  }

  public String createResetToken(User user) {
    String token = UUID.randomUUID().toString();
    user.setResetToken(token);
    user.setResetExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
    repo.save(user);
    return token;
  }

  public Optional<User> findByResetToken(String token) {
    return repo.findByResetToken(token);
  }

  @PostConstruct
  public void ensureDefaultAdmin() {
    String adminUsername = "admin";
    if (repo.findByUsername(adminUsername).isEmpty()) {
      User u = new User();
      u.setUsername(adminUsername);
      u.setEmail("admin@local");
      u.setPasswordHash(encoder.encode("admin"));
      u.setRole("DONO");
      repo.save(u);
      System.out.println(
          "Created default admin user 'admin' with password 'admin' (change in production)");
    }
  }
}
