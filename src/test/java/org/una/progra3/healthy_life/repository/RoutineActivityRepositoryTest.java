package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.RoutineActivity;

public class RoutineActivityRepositoryTest {
    @Test
    void testFindById() {
        RoutineActivityRepository repo = Mockito.mock(RoutineActivityRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new RoutineActivity()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
