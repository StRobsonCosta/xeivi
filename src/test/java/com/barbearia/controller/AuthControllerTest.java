package com.barbearia.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barbearia.dto.AuthRequest;
import com.barbearia.dto.ResetPasswordRequest;
import com.barbearia.model.User;
import com.barbearia.security.JwtUtil;
import com.barbearia.service.UserService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class AuthControllerTest {

  @Test
  void loginRejectsWhenRequestedRoleDoesNotMatchUserRole() {
    UserService userService = mock(UserService.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    AuthController controller =
        new AuthController(userService, jwtUtil, mailSender, "http://localhost:4200");

    User user = new User();
    user.setUsername("cliente");
    user.setPasswordHash("hash");
    user.setRole("CLIENTE");

    when(userService.findByUsername("cliente")).thenReturn(Optional.of(user));
    when(userService.checkPassword(user, "123456")).thenReturn(true);

    AuthRequest req = new AuthRequest();
    req.setUsername("cliente");
    req.setPassword("123456");
    req.setRole("DONO");

    ResponseEntity<?> response = controller.login(req);

    assertEquals(401, response.getStatusCode().value());
    verify(jwtUtil, never()).generateToken(anyString(), anyString());
  }

  @Test
  void loginAllowsWhenRequestedRoleMatchesUserRole() {
    UserService userService = mock(UserService.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    AuthController controller =
        new AuthController(userService, jwtUtil, mailSender, "http://localhost:4200");

    User user = new User();
    user.setUsername("barbeiro");
    user.setPasswordHash("hash");
    user.setRole("BARBEIRO");

    when(userService.findByUsername("barbeiro")).thenReturn(Optional.of(user));
    when(userService.checkPassword(user, "123456")).thenReturn(true);
    when(jwtUtil.generateToken("barbeiro", "BARBEIRO")).thenReturn("token");

    AuthRequest req = new AuthRequest();
    req.setUsername("barbeiro");
    req.setPassword("123456");
    req.setRole("BARBEIRO");

    ResponseEntity<?> response = controller.login(req);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    verify(jwtUtil).generateToken("barbeiro", "BARBEIRO");
  }

  @Test
  void requestResetSendsEmailWhenUserExists() {
    UserService userService = mock(UserService.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    AuthController controller =
        new AuthController(userService, jwtUtil, mailSender, "http://localhost:4200");

    User user = new User();
    user.setEmail("cliente@barbearia.com");
    when(userService.findByEmail("cliente@barbearia.com")).thenReturn(Optional.of(user));
    when(userService.createResetToken(user)).thenReturn("reset-token-123");

    ResponseEntity<?> response = controller.requestReset("cliente@barbearia.com");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("reset-token-123", response.getBody());
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  void resetChangesPasswordAndClearsResetToken() {
    UserService userService = mock(UserService.class);
    JwtUtil jwtUtil = mock(JwtUtil.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    AuthController controller =
        new AuthController(userService, jwtUtil, mailSender, "http://localhost:4200");

    User user = new User();
    user.setUsername("cliente");
    user.setResetToken("token-ativo");
    user.setResetExpiresAt(Instant.now().plusSeconds(600));

    ResetPasswordRequest req = new ResetPasswordRequest();
    req.setToken("token-ativo");
    req.setNewPassword("novaSenha123");

    when(userService.findByResetToken("token-ativo")).thenReturn(Optional.of(user));

    ResponseEntity<?> response = controller.reset(req);

    assertEquals(200, response.getStatusCode().value());
    verify(userService).updatePassword(user, "novaSenha123");
    verify(userService).clearResetToken(user);
  }
}
