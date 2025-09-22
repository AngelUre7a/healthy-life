package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.User;

public class UserRepositoryTest {
    @Test
    void testFindById() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new User()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
