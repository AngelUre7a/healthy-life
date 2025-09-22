package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.ProgressLog;

public class ProgressLogRepositoryTest {
    @Test
    void testFindById() {
        ProgressLogRepository repo = Mockito.mock(ProgressLogRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new ProgressLog()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
