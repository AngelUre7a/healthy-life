package org.una.progra3.healthy_life.security.graphql;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.security.graphql.responses.LoginResponse;
import org.una.progra3.healthy_life.service.AuthenticationService;

@ExtendWith(MockitoExtension.class)
public class AuthResolverTest {
    @Mock AuthenticationService authenticationService;
    @InjectMocks AuthResolver authResolver;

    @Test
    void testLogin() {
        Mockito.when(authenticationService.login("mail", "pass")).thenReturn(new LoginResponse("token", "mail", "name", "role"));
        LoginResponse response = authResolver.login("mail", "pass");
        assertNotNull(response);
        assertEquals("token", response.getToken());
    }
}
