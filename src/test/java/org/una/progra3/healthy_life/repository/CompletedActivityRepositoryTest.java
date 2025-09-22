package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.CompletedActivity;

public class CompletedActivityRepositoryTest {
    @Test
    void testFindById() {
        CompletedActivityRepository repo = Mockito.mock(CompletedActivityRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new CompletedActivity()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
