package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Role;

public class RoleRepositoryTest {
    @Test
    void testFindById() {
        RoleRepository repo = Mockito.mock(RoleRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Role()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
