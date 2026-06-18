package com.barbearia.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String username;
    private Long customerId;

    public AuthResponse() {}
    public AuthResponse(String token, String role, String username, Long customerId) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.customerId = customerId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
