package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.repository.RoutineRepository;
import org.una.progra3.healthy_life.entity.User;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoutineServiceTest {
    @Mock
    RoutineRepository routineRepository;
    @InjectMocks
    RoutineService routineService;

    @Test
    void testFindById() {
        Routine routine = new Routine();
        routine.setId(1L);
        Mockito.when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        Routine result = routineService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(routineRepository.findById(2L)).thenReturn(Optional.empty());
        Routine result = routineService.findById(2L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Routine routine = new Routine();
        Mockito.when(routineRepository.findAll()).thenReturn(List.of(routine));
        List<Routine> result = routineService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUser() {
        User user = new User();
        Routine routine = new Routine();
        Mockito.when(routineRepository.findByUser(user)).thenReturn(List.of(routine));
        List<Routine> result = routineService.findByUser(user);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateSuccess() {
        Routine routine = new Routine();
        routine.setTitle("Rutina");
        User user = new User();
        routine.setUser(user);
        Mockito.when(routineRepository.save(routine)).thenReturn(routine);
        Routine result = routineService.create(routine);
        assertNotNull(result);
    }

    @Test
    void testCreateMissingTitle() {
        Routine routine = new Routine();
        User user = new User();
        routine.setUser(user);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineService.create(routine));
        assertEquals("Routine title is required", ex.getMessage());
    }

    @Test
    void testCreateMissingUser() {
        Routine routine = new Routine();
        routine.setTitle("Rutina");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineService.create(routine));
        assertEquals("Routine user is required", ex.getMessage());
    }

    @Test
    void testUpdateSuccess() {
        Routine routine = new Routine();
        routine.setId(1L);
        Mockito.when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        Mockito.when(routineRepository.save(routine)).thenReturn(routine);
        Routine result = routineService.update(1L, "Nueva rutina", "L,M,X");
        assertNotNull(result);
        assertEquals("Nueva rutina", result.getTitle());
        assertEquals("L,M,X", result.getDaysOfWeek());
    }

    @Test
    void testUpdateNotFound() {
        Mockito.when(routineRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> routineService.update(2L, "Nueva rutina", "L,M,X"));
        assertEquals("Routine not found", ex.getMessage());
    }

    @Test
    void testUpdateAllNull_NoChanges() {
        Routine routine = new Routine();
        routine.setId(1L);
        routine.setTitle("Old");
        routine.setDaysOfWeek("L,M");
        Mockito.when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        Mockito.when(routineRepository.save(routine)).thenReturn(routine);
        Routine result = routineService.update(1L, null, null);
        assertNotNull(result);
        assertEquals("Old", result.getTitle());
        assertEquals("L,M", result.getDaysOfWeek());
    }

    @Test
    void testDeleteByIdSuccess() {
        Mockito.when(routineRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(routineRepository).deleteById(1L);
        boolean result = routineService.deleteById(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Mockito.when(routineRepository.existsById(2L)).thenReturn(false);
        boolean result = routineService.deleteById(2L);
        assertFalse(result);
    }
}
