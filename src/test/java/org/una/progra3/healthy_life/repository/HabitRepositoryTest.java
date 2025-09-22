package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Habit;

public class HabitRepositoryTest {
    @Test
    void testFindById() {
        HabitRepository habitRepository = Mockito.mock(HabitRepository.class);
        Mockito.when(habitRepository.findById(1L)).thenReturn(java.util.Optional.of(new Habit()));
        assertTrue(habitRepository.findById(1L).isPresent());
    }
}
