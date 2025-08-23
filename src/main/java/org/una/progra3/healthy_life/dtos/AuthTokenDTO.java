package org.una.progra3.healthy_life.dtos;

import java.time.LocalDateTime;

public class AuthTokenDTO {
    private Long id;
    private String token;
    private LocalDateTime expiresAt;
    private Long userId;

    public AuthTokenDTO() {}

    public Long getId() {
         return id; 
    }

    public void setId(Long id) {
         this.id = id; 
    }

    public String getToken() {
         return token; 
    }

    public void setToken(String token) {
         this.token = token; 
    }

    public LocalDateTime getExpiresAt() {
         return expiresAt; 
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
         this.expiresAt = expiresAt; 
    }

    public Long getUserId() {
         return userId; 
    }

    public void setUserId(Long userId) {
         this.userId = userId; 
    }
}
