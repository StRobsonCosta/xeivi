package com.kavex.barbearia.service;

import com.kavex.barbearia.model.User;
import com.kavex.barbearia.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${security.jwt.expirationMs}")
    private long jwtExpiration;

    public UserService(UserRepository repo) { this.repo = repo; }

    public User register(String username, String email, String password, String role) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(role == null ? "CLIENTE" : role);
        return repo.save(u);
    }

    public Optional<User> findByUsername(String username) { return repo.findByUsername(username); }

    public Optional<User> findById(Long id) { return repo.findById(id); }

    public List<User> findAll() { return repo.findAll(); }

    public boolean checkPassword(User user, String rawPassword) {
        return encoder.matches(rawPassword, user.getPasswordHash());
    }

    public void updatePassword(User user, String newPassword) {
        user.setPasswordHash(encoder.encode(newPassword));
        repo.save(user);
    }

    public String createResetToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        repo.save(user);
        return token;
    }

    public Optional<User> findByResetToken(String token) { return repo.findByResetToken(token); }
}
