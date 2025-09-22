package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.repository.ProgressLogRepository;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Routine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProgressLogServiceTest {
    @Mock
    ProgressLogRepository progressLogRepository;
    @InjectMocks
    ProgressLogService progressLogService;

    @Test
    void testFindById() {
        ProgressLog pl = new ProgressLog();
        pl.setId(1L);
        Mockito.when(progressLogRepository.findById(1L)).thenReturn(Optional.of(pl));
        ProgressLog result = progressLogService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(progressLogRepository.findById(2L)).thenReturn(Optional.empty());
        ProgressLog result = progressLogService.findById(2L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        ProgressLog pl = new ProgressLog();
        Mockito.when(progressLogRepository.findAll()).thenReturn(List.of(pl));
        List<ProgressLog> result = progressLogService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUser() {
        User user = new User();
        ProgressLog pl = new ProgressLog();
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(pl));
        List<ProgressLog> result = progressLogService.findByUser(user);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByRoutine() {
        Routine routine = new Routine();
        ProgressLog pl = new ProgressLog();
        Mockito.when(progressLogRepository.findByRoutine(routine)).thenReturn(List.of(pl));
        List<ProgressLog> result = progressLogService.findByRoutine(routine);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserAndDate() {
        User user = new User();
        LocalDate date = LocalDate.now();
        ProgressLog pl = new ProgressLog();
        Mockito.when(progressLogRepository.findByUserAndDate(user, date)).thenReturn(List.of(pl));
        List<ProgressLog> result = progressLogService.findByUserAndDate(user, date);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateSuccess() {
        ProgressLog pl = new ProgressLog();
        User user = new User();
        pl.setUser(user);
        pl.setDate(LocalDate.now());
        Mockito.when(progressLogRepository.save(pl)).thenReturn(pl);
        ProgressLog result = progressLogService.create(pl);
        assertNotNull(result);
    }

    @Test
    void testCreateMissingUser() {
        ProgressLog pl = new ProgressLog();
        pl.setDate(LocalDate.now());
        Exception ex = assertThrows(IllegalArgumentException.class, () -> progressLogService.create(pl));
        assertEquals("User is required", ex.getMessage());
    }

    @Test
    void testCreateMissingDate() {
        ProgressLog pl = new ProgressLog();
        User user = new User();
        pl.setUser(user);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> progressLogService.create(pl));
        assertEquals("Date is required", ex.getMessage());
    }

    @Test
    void testDeleteByIdSuccess() {
        Mockito.when(progressLogRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(progressLogRepository).deleteById(1L);
        boolean result = progressLogService.deleteById(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Mockito.when(progressLogRepository.existsById(2L)).thenReturn(false);
        boolean result = progressLogService.deleteById(2L);
        assertFalse(result);
    }
}
