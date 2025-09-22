package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Routine;

public class RoutineRepositoryTest {
    @Test
    void testFindById() {
        RoutineRepository repo = Mockito.mock(RoutineRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Routine()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
