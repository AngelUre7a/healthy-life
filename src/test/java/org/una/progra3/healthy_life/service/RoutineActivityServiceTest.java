package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.RoutineActivity;
import org.una.progra3.healthy_life.repository.RoutineActivityRepository;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.Habit;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RoutineActivityServiceTest {
    @Mock
    RoutineActivityRepository routineActivityRepository;
    @InjectMocks
    RoutineActivityService routineActivityService;

    @Test
    void testFindById() {
        RoutineActivity ra = new RoutineActivity();
        ra.setId(1L);
        Mockito.when(routineActivityRepository.findById(1L)).thenReturn(Optional.of(ra));
        RoutineActivity result = routineActivityService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(routineActivityRepository.findById(2L)).thenReturn(Optional.empty());
        RoutineActivity result = routineActivityService.findById(2L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        RoutineActivity ra = new RoutineActivity();
        Mockito.when(routineActivityRepository.findAll()).thenReturn(List.of(ra));
        List<RoutineActivity> result = routineActivityService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByRoutine() {
        Routine routine = new Routine();
        RoutineActivity ra = new RoutineActivity();
        Mockito.when(routineActivityRepository.findByRoutine(routine)).thenReturn(List.of(ra));
        List<RoutineActivity> result = routineActivityService.findByRoutine(routine);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateSuccess() {
        Routine routine = new Routine();
        Habit habit = new Habit();
        Integer duration = 30;
        LocalTime targetTime = LocalTime.of(8, 0);
        String notes = "Test notes";
        RoutineActivity ra = new RoutineActivity();
        Mockito.when(routineActivityRepository.save(Mockito.any(RoutineActivity.class))).thenReturn(ra);
        RoutineActivity result = routineActivityService.create(routine, habit, duration, targetTime, notes);
        assertNotNull(result);
    }

    @Test
    void testCreateMissingRoutine() {
        Habit habit = new Habit();
        Integer duration = 30;
        LocalTime targetTime = LocalTime.of(8, 0);
        String notes = "Test notes";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineActivityService.create(null, habit, duration, targetTime, notes));
        assertEquals("Routine is required", ex.getMessage());
    }

    @Test
    void testCreateMissingHabit() {
        Routine routine = new Routine();
        Integer duration = 30;
        LocalTime targetTime = LocalTime.of(8, 0);
        String notes = "Test notes";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineActivityService.create(routine, null, duration, targetTime, notes));
        assertEquals("Habit is required", ex.getMessage());
    }

    @Test
    void testCreateNullDuration() {
        Routine routine = new Routine();
        Habit habit = new Habit();
        LocalTime targetTime = LocalTime.of(8, 0);
        String notes = "Test notes";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineActivityService.create(routine, habit, null, targetTime, notes));
        assertEquals("Duration must be non-negative", ex.getMessage());
    }

    @Test
    void testCreateNegativeDuration() {
        Routine routine = new Routine();
        Habit habit = new Habit();
        LocalTime targetTime = LocalTime.of(8, 0);
        String notes = "Test notes";
        Exception ex = assertThrows(IllegalArgumentException.class, () -> routineActivityService.create(routine, habit, -5, targetTime, notes));
        assertEquals("Duration must be non-negative", ex.getMessage());
    }

    @Test
    void testUpdateSuccess() {
        RoutineActivity ra = new RoutineActivity();
        ra.setId(1L);
        Mockito.when(routineActivityRepository.findById(1L)).thenReturn(Optional.of(ra));
        Mockito.when(routineActivityRepository.save(ra)).thenReturn(ra);
        RoutineActivity result = routineActivityService.update(1L, 45, LocalTime.of(9,0), "Updated notes");
        assertNotNull(result);
        assertEquals(45, result.getDuration());
        assertEquals(LocalTime.of(9,0), result.getTargetTime());
        assertEquals("Updated notes", result.getNotes());
    }

    @Test
    void testUpdateNotFound() {
        Mockito.when(routineActivityRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> routineActivityService.update(2L, 45, LocalTime.of(9,0), "Updated notes"));
        assertEquals("RoutineActivity not found", ex.getMessage());
    }

    @Test
    void testUpdateAllNull_NoChanges() {
        RoutineActivity ra = new RoutineActivity();
        ra.setId(1L);
        ra.setDuration(30);
        ra.setTargetTime(LocalTime.of(7, 30));
        ra.setNotes("Initial");
        Mockito.when(routineActivityRepository.findById(1L)).thenReturn(Optional.of(ra));
        Mockito.when(routineActivityRepository.save(ra)).thenReturn(ra);
        RoutineActivity result = routineActivityService.update(1L, null, null, null);
        assertNotNull(result);
        assertEquals(30, result.getDuration());
        assertEquals(LocalTime.of(7, 30), result.getTargetTime());
        assertEquals("Initial", result.getNotes());
    }

    @Test
    void testDeleteByIdSuccess() {
        Mockito.when(routineActivityRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(routineActivityRepository).deleteById(1L);
        boolean result = routineActivityService.deleteById(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Mockito.when(routineActivityRepository.existsById(2L)).thenReturn(false);
        boolean result = routineActivityService.deleteById(2L);
        assertFalse(result);
    }
}
