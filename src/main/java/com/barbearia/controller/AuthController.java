package com.barbearia.controller;

import com.barbearia.dto.*;
import com.barbearia.model.User;
import com.barbearia.security.JwtUtil;
import com.barbearia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final String frontendUrl;

    public AuthController(UserService userService,
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
        Optional<User> uopt = userService.findByUsername(req.getUsername());
        if (uopt.isEmpty()) return ResponseEntity.status(401).build();
        User u = uopt.get();
        if (!userService.checkPassword(u, req.getPassword())) return ResponseEntity.status(401).build();
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(new AuthResponse(token, u.getRole()));
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
        u.setResetToken(null);
        u.setResetExpiresAt(null);
        return ResponseEntity.ok().build();
    }
}
