package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.security.graphql.responses.LoginResponse;
import org.una.progra3.healthy_life.security.jwt.JwtTokenProvider;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(String email, String password) {
        User user = userService.login(email, password);
        if (user == null) {
            throw new RuntimeException("Credenciales inválidas");
        }
        String role = user.getRole() != null ? user.getRole().getName() : null;
        String token = jwtTokenProvider.generateAccessToken(
            user.getEmail(), // username
            user.getEmail(), // email
            user.getId(),    // id
            role             // role
        );
        return new LoginResponse(token, user.getEmail(), user.getName(), role);
    }
}
