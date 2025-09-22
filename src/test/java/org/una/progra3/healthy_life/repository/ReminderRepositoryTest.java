package org.una.progra3.healthy_life.repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Reminder;

public class ReminderRepositoryTest {
    @Test
    void testFindById() {
        ReminderRepository repo = Mockito.mock(ReminderRepository.class);
        Mockito.when(repo.findById(1L)).thenReturn(java.util.Optional.of(new Reminder()));
        assertTrue(repo.findById(1L).isPresent());
    }
}
