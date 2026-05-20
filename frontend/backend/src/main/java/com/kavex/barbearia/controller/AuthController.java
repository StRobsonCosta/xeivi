package com.kavex.barbearia.controller;

import com.kavex.barbearia.dto.*;
import com.kavex.barbearia.model.User;
import com.kavex.barbearia.security.JwtUtil;
import com.kavex.barbearia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
        // TODO: send email with token link. For now return token in response for dev/testing.
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
