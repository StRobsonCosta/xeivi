package com.barbearia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  @Value("${security.jwt.secret:ChangeMeForProd}")
  private String secret;

  @Value("${security.jwt.expirationMs:3600000}")
  private long expirationMs;

  private Key signingKey() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    // Ensure key is at least 32 bytes for HS256; pad or truncate as needed
    if (keyBytes.length < 32) {
      keyBytes = Arrays.copyOf(keyBytes, 32);
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String username, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(signingKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token).getBody();
  }
}
