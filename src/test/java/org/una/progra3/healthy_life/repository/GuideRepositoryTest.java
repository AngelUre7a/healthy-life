package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Guide;

public class GuideRepositoryTest {
    @Test
    void testFindById() {
        GuideRepository repo = Mockito.mock(GuideRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Guide()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
