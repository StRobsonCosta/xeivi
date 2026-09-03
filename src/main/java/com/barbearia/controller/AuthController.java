package com.barbearia.controller;

import com.barbearia.dto.*;
import com.barbearia.model.User;
import com.barbearia.security.JwtUtil;
import com.barbearia.service.UserService;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final JavaMailSender mailSender;
  private final String frontendUrl;

  public AuthController(
      UserService userService,
      JwtUtil jwtUtil,
      JavaMailSender mailSender,
      @Value("${app.frontend.url:http://localhost:4200}") String frontendUrl) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
    this.mailSender = mailSender;
    this.frontendUrl = frontendUrl;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    if (userService.findByUsername(req.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().body("username_taken");
    }
    userService.register(req.getUsername(), req.getEmail(), req.getPassword(), req.getRole());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest req) {
    System.out.println("Login attempt for username='" + req.getUsername() + "'");
    // Try to find by username first, then by email to allow login using either
    Optional<User> uopt = userService.findByUsername(req.getUsername());
    boolean lookedUpByEmail = false;
    if (uopt.isEmpty()) {
      uopt = userService.findByEmail(req.getUsername());
      lookedUpByEmail = true;
    }
    if (uopt.isEmpty()) {
      System.out.println(
          "Login failed: user not found (lookup='"
              + (lookedUpByEmail ? "email" : "username")
              + "'): '"
              + req.getUsername()
              + "'");
      return ResponseEntity.status(401).build();
    }
    User u = uopt.get();
    if (!userService.checkPassword(u, req.getPassword())) {
      System.out.println("Login failed: invalid password for user: '" + req.getUsername() + "'");
      return ResponseEntity.status(401).build();
    }

    String requestedRole = req.getRole();
    if (requestedRole != null && !requestedRole.isBlank()) {
      String normalizedRequestedRole = requestedRole.trim().toUpperCase(Locale.ROOT);
      String normalizedUserRole =
          u.getRole() == null ? null : u.getRole().trim().toUpperCase(Locale.ROOT);
      if (normalizedUserRole == null || !normalizedRequestedRole.equals(normalizedUserRole)) {
        System.out.println(
            "Login failed: role mismatch for user='"
                + req.getUsername()
                + "', requestedRole='"
                + requestedRole
                + "', userRole='"
                + u.getRole()
                + "'");
        return ResponseEntity.status(401).build();
      }
    }

    String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
    System.out.println(
        "Login successful for user='"
            + req.getUsername()
            + "', role='"
            + u.getRole()
            + "', customerId='"
            + u.getCustomerId()
            + "'");
    return ResponseEntity.ok(
        new AuthResponse(token, u.getRole(), u.getUsername(), u.getCustomerId()));
  }

  @PostMapping("/request-reset")
  public ResponseEntity<?> requestReset(@RequestParam("email") String email) {
    Optional<User> uopt = userService.findByEmail(email);
    if (uopt.isEmpty()) return ResponseEntity.ok().build(); // do not reveal
    User u = uopt.get();
    String token = userService.createResetToken(u);
    try {
      String resetLink = frontendUrl + "/reset?token=" + token;
      SimpleMailMessage msg = new SimpleMailMessage();
      msg.setTo(u.getEmail());
      msg.setSubject("Password reset");
      msg.setText("Use the following link to reset your password: " + resetLink);
      mailSender.send(msg);
    } catch (Exception e) {
      System.err.println("Failed to send reset email: " + e.getMessage());
    }
    return ResponseEntity.ok().body(token);
  }

  @PostMapping("/reset")
  public ResponseEntity<?> reset(@RequestBody ResetPasswordRequest req) {
    Optional<User> uopt = userService.findByResetToken(req.getToken());
    if (uopt.isEmpty()) return ResponseEntity.badRequest().body("invalid_token");
    User u = uopt.get();
    if (u.getResetExpiresAt() == null || u.getResetExpiresAt().isBefore(Instant.now())) {
      return ResponseEntity.badRequest().body("expired_token");
    }
    userService.updatePassword(u, req.getNewPassword());
    userService.clearResetToken(u);
    return ResponseEntity.ok().build();
  }
}
