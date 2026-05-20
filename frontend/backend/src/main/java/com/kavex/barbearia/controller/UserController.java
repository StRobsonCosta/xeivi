package com.kavex.barbearia.controller;

import com.kavex.barbearia.model.User;
import com.kavex.barbearia.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public List<User> all() { return userService.findAll(); }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        String username = auth.getName();
        Optional<User> u = userService.findByUsername(username);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication auth, @RequestBody com.kavex.barbearia.dto.ChangePasswordRequest req) {
        if (auth == null) return ResponseEntity.status(401).build();
        Optional<User> uopt = userService.findByUsername(auth.getName());
        if (uopt.isEmpty()) return ResponseEntity.status(401).build();
        var user = uopt.get();
        if (!userService.checkPassword(user, req.getOldPassword())) return ResponseEntity.badRequest().body("invalid_current_password");
        userService.updatePassword(user, req.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
