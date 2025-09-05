package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.AuthToken;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.AuthTokenRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    public List<AuthToken> findAll() { return authTokenRepository.findAll(); }
    public AuthToken findById(Long id) { return authTokenRepository.findById(id).orElse(null); }
    public AuthToken findByToken(String token) { return authTokenRepository.findByToken(token).orElse(null); }
    public List<AuthToken> findByUser(User user) { return authTokenRepository.findByUser(user); }

    @Transactional
    public AuthToken create(User user, String token, LocalDateTime expiresAt) {
        if (user == null) throw new IllegalArgumentException("User is required");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Token is required");
        AuthToken newToken = new AuthToken();
        newToken.setUser(user);
        newToken.setToken(token);
        newToken.setExpiresAt(expiresAt);
        return authTokenRepository.save(newToken);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (authTokenRepository.existsById(id)) { authTokenRepository.deleteById(id); return true; }
        return false;
    }
}
