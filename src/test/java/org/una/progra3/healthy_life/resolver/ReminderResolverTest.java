package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.Reminder;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;
import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.service.HabitService;
import org.una.progra3.healthy_life.service.ReminderService;
import org.una.progra3.healthy_life.service.UserService;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderResolverTest {
    @Mock ReminderService reminderService;
    @Mock UserService userService;
    @Mock HabitService habitService;
    @Mock AuthenticationService authenticationService;
    @InjectMocks ReminderResolver reminderResolver;

    // query
    @Test
    void testRemindersByUser_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(1L)).thenReturn(current);
        when(reminderService.findByUser(current)).thenReturn(List.of(new Reminder()));
        List<Reminder> result = reminderResolver.remindersByUser(1L);
        assertEquals(1, result.size());
        verify(reminderService).findByUser(current);
    }

    @Test
    void testRemindersByUser_userNotFound_returnsEmpty() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(2L)).thenReturn(null);
        List<Reminder> result = reminderResolver.remindersByUser(2L);
        assertTrue(result.isEmpty());
        verifyNoInteractions(reminderService);
    }

    @Test
    void testRemindersByUser_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> reminderResolver.remindersByUser(1L));
    }

    // mutations
    @Test
    void testCreateReminder_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(3L)).thenReturn(current);
        Habit habit = new Habit(); when(habitService.findById(4L)).thenReturn(habit);
        Reminder rem = new Reminder();
        when(reminderService.create(eq(current), eq(habit), any(), eq(ReminderFrequency.DAILY))).thenReturn(rem);
        Reminder out = reminderResolver.createReminder(3L, 4L, "08:30", ReminderFrequency.DAILY);
        assertNotNull(out);
        verify(reminderService).create(eq(current), eq(habit), any(), eq(ReminderFrequency.DAILY));
    }

    @Test
    void testCreateReminder_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> reminderResolver.createReminder(1L, 2L, "09:00", ReminderFrequency.WEEKLY));
        verifyNoInteractions(userService, habitService, reminderService);
    }

    @Test
    void testUpdateReminder_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        Reminder rem = new Reminder();
        when(reminderService.update(10L, LocalTime.parse("10:00"), ReminderFrequency.WEEKLY)).thenReturn(rem);
        Reminder out = reminderResolver.updateReminder(10L, "10:00", ReminderFrequency.WEEKLY);
        assertNotNull(out);
    }

    @Test
    void testUpdateReminder_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> reminderResolver.updateReminder(10L, "10:00", ReminderFrequency.WEEKLY));
        verify(reminderService, never()).update(anyLong(), any(), any());
    }

    @Test
    void testUpdateReminder_timeNull_passesNullToService() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        Reminder rem = new Reminder();
        when(reminderService.update(5L, null, ReminderFrequency.DAILY)).thenReturn(rem);
        Reminder out = reminderResolver.updateReminder(5L, null, ReminderFrequency.DAILY);
        assertNotNull(out);
        verify(reminderService).update(5L, null, ReminderFrequency.DAILY);
    }

    @Test
    void testCreateReminder_invalidTime_throws() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(1L)).thenReturn(current);
        Habit habit = new Habit(); when(habitService.findById(2L)).thenReturn(habit);
        assertThrows(DateTimeParseException.class, () -> reminderResolver.createReminder(1L, 2L, "25:61", ReminderFrequency.DAILY));
        verifyNoInteractions(reminderService);
    }

    @Test
    void testUpdateReminder_invalidTime_throws() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(DateTimeParseException.class, () -> reminderResolver.updateReminder(9L, "99:99", ReminderFrequency.WEEKLY));
        verify(reminderService, never()).update(anyLong(), any(), any());
    }

    @Test
    void testDeleteReminder_happyPath() {
        Role role = new Role(); role.setCanDelete(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(reminderService.deleteById(8L)).thenReturn(true);
        assertTrue(reminderResolver.deleteReminder(8L));
        verify(reminderService).deleteById(8L);
    }

    @Test
    void testDeleteReminder_noDeletePermission_throws() {
        Role role = new Role(); role.setCanDelete(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> reminderResolver.deleteReminder(8L));
        verify(reminderService, never()).deleteById(anyLong());
    }
}
