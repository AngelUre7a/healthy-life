package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Reminder;
import org.una.progra3.healthy_life.repository.ReminderRepository;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {
    @Mock
    ReminderRepository reminderRepository;
    @InjectMocks
    ReminderService reminderService;

    @Test
    void testFindById() {
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        Mockito.when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
        Reminder result = reminderService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(reminderRepository.findById(2L)).thenReturn(Optional.empty());
        Reminder result = reminderService.findById(2L);
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Reminder reminder = new Reminder();
        Mockito.when(reminderRepository.findAll()).thenReturn(List.of(reminder));
        List<Reminder> result = reminderService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUser() {
        User user = new User();
        Reminder reminder = new Reminder();
        Mockito.when(reminderRepository.findByUser(user)).thenReturn(List.of(reminder));
        List<Reminder> result = reminderService.findByUser(user);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByHabit() {
        Habit habit = new Habit();
        Reminder reminder = new Reminder();
        Mockito.when(reminderRepository.findByHabit(habit)).thenReturn(List.of(reminder));
        List<Reminder> result = reminderService.findByHabit(habit);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserAndFrequency() {
        User user = new User();
        Reminder reminder = new Reminder();
        Mockito.when(reminderRepository.findByUserAndFrequency(user, ReminderFrequency.DAILY)).thenReturn(List.of(reminder));
        List<Reminder> result = reminderService.findByUserAndFrequency(user, ReminderFrequency.DAILY);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateSuccess() {
        User user = new User();
        Habit habit = new Habit();
        LocalTime time = LocalTime.of(8, 0);
        ReminderFrequency freq = ReminderFrequency.DAILY;
        Reminder reminder = new Reminder();
        Mockito.when(reminderRepository.save(Mockito.any(Reminder.class))).thenReturn(reminder);
        Reminder result = reminderService.create(user, habit, time, freq);
        assertNotNull(result);
    }

    @Test
    void testCreateMissingUser() {
        Habit habit = new Habit();
        LocalTime time = LocalTime.of(8, 0);
        ReminderFrequency freq = ReminderFrequency.DAILY;
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reminderService.create(null, habit, time, freq));
        assertEquals("User is required", ex.getMessage());
    }

    @Test
    void testCreateMissingHabit() {
        User user = new User();
        LocalTime time = LocalTime.of(8, 0);
        ReminderFrequency freq = ReminderFrequency.DAILY;
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reminderService.create(user, null, time, freq));
        assertEquals("Habit is required", ex.getMessage());
    }

    @Test
    void testCreateMissingTime() {
        User user = new User();
        Habit habit = new Habit();
        ReminderFrequency freq = ReminderFrequency.DAILY;
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reminderService.create(user, habit, null, freq));
        assertEquals("Time is required", ex.getMessage());
    }

    @Test
    void testCreateMissingFrequency() {
        User user = new User();
        Habit habit = new Habit();
        LocalTime time = LocalTime.of(8, 0);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reminderService.create(user, habit, time, null));
        assertEquals("Frequency is required", ex.getMessage());
    }

    @Test
    void testUpdateSuccess() {
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        Mockito.when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
        Mockito.when(reminderRepository.save(reminder)).thenReturn(reminder);
        Reminder result = reminderService.update(1L, LocalTime.of(9,0), ReminderFrequency.WEEKLY);
        assertNotNull(result);
        assertEquals(ReminderFrequency.WEEKLY, result.getFrequency());
        assertEquals(LocalTime.of(9,0), result.getTime());
    }

    @Test
    void testUpdateNotFound() {
        Mockito.when(reminderRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> reminderService.update(2L, LocalTime.of(9,0), ReminderFrequency.WEEKLY));
        assertEquals("Reminder not found", ex.getMessage());
    }

    @Test
    void testUpdateAllNull_NoChanges() {
        Reminder reminder = new Reminder();
        reminder.setId(1L);
        reminder.setTime(LocalTime.of(8, 0));
        reminder.setFrequency(ReminderFrequency.DAILY);
        Mockito.when(reminderRepository.findById(1L)).thenReturn(Optional.of(reminder));
        Mockito.when(reminderRepository.save(reminder)).thenReturn(reminder);
        Reminder result = reminderService.update(1L, null, null);
        assertNotNull(result);
        assertEquals(LocalTime.of(8, 0), result.getTime());
        assertEquals(ReminderFrequency.DAILY, result.getFrequency());
    }

    @Test
    void testDeleteByIdSuccess() {
        Mockito.when(reminderRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(reminderRepository).deleteById(1L);
        boolean result = reminderService.deleteById(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Mockito.when(reminderRepository.existsById(2L)).thenReturn(false);
        boolean result = reminderService.deleteById(2L);
        assertFalse(result);
    }
}
