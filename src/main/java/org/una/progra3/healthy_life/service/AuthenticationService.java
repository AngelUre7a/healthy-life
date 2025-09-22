package org.una.progra3.healthy_life.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.security.graphql.responses.LoginResponse;
import org.una.progra3.healthy_life.security.jwt.JwtTokenProvider;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(String email, String password) {
        User user = userService.login(email, password);
        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }
        String role = user.getRole() != null ? user.getRole().getName().name() : null;
        String token = jwtTokenProvider.generateAccessToken(
            user.getEmail(), // username
            user.getEmail(), // email
            user.getId(),    // id
            role             // role
        );
        // Imprimir el token para facilitar pruebas manuales (no recomendado en producción)
        log.info("JWT Token generado para {}: {}", email, token);
        System.out.println("JWT Token: " + token);
        return new LoginResponse(token, user.getEmail(), user.getName(), role);
    }

    /**
     * Returns the currently authenticated User based on JWT in the request header.
     */
    public User getCurrentUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new RuntimeException("No request context available");
        HttpServletRequest request = attrs.getRequest();
        String token = jwtTokenProvider.resolveToken(request).orElseThrow(() -> new RuntimeException("No JWT token found"));
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) throw new RuntimeException("Invalid JWT token: no user ID");
        User user = userService.findById(userId);
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }
}
