package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.CompletedActivity;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgressResolverTest {
    @Mock ProgressLogService progressLogService;
    @Mock CompletedActivityService completedActivityService;
    @Mock RoutineService routineService;
    @Mock UserService userService;
    @Mock HabitService habitService;
    @Mock ProgressStatisticsService progressStatisticsService;
    @Mock AuthenticationService authenticationService;
    @InjectMocks ProgressResolver progressResolver;

    // progressLogByDate
    @Test
    void testProgressLogByDate_happyPath() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(userService.findById(1L)).thenReturn(user);
        when(progressLogService.findByUserAndDate(user, LocalDate.parse("2023-01-01")))
                .thenReturn(List.of(new ProgressLog()));
        ProgressLog result = progressResolver.progressLogByDate(1L, "2023-01-01");
        assertNotNull(result);
    }

    @Test
    void testProgressLogByDate_userNotFound_returnsNull() {
        Role role = new Role();
        role.setCanRead(true);
        User current = new User();
        current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(99L)).thenReturn(null);
        ProgressLog result = progressResolver.progressLogByDate(99L, "2024-05-01");
        assertNull(result);
        verifyNoInteractions(progressLogService);
    }

    @Test
    void testProgressLogByDate_emptyList_returnsNull() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(userService.findById(1L)).thenReturn(user);
        when(progressLogService.findByUserAndDate(user, LocalDate.parse("2024-01-02")))
                .thenReturn(List.of());
        ProgressLog result = progressResolver.progressLogByDate(1L, "2024-01-02");
        assertNull(result);
    }

    @Test
    void testProgressLogByDate_noReadPermission_throws() {
        Role role = new Role();
        role.setCanRead(false);
        User current = new User();
        current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> progressResolver.progressLogByDate(1L, "2023-01-01"));
    }

    @Test
    void testProgressLogByDate_invalidDate_throws() {
        Role role = new Role();
        role.setCanRead(true);
        User current = new User();
        current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(1L)).thenReturn(current);
        assertThrows(DateTimeParseException.class, () -> progressResolver.progressLogByDate(1L, "not-a-date"));
    }

    // weeklyProgress
    @Test
    void testWeeklyProgress_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(10L)).thenReturn(current);
        LocalDate start = LocalDate.parse("2024-06-03");
        Map<LocalDate, Long> map = new LinkedHashMap<>();
        map.put(start, 2L); map.put(start.plusDays(1), 0L);
        when(progressStatisticsService.weeklyCompletedCounts(current, start)).thenReturn(map);

        List<DailyProgress> result = progressResolver.weeklyProgress(10L, "2024-06-03");
        assertEquals(2, result.size());
        assertEquals("2024-06-03", result.get(0).date());
        assertEquals(2, result.get(0).completedCount());
        assertEquals("2024-06-04", result.get(1).date());
        assertEquals(0, result.get(1).completedCount());
    }

    @Test
    void testWeeklyProgress_userNotFound_returnsEmpty() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(10L)).thenReturn(null);
        List<DailyProgress> result = progressResolver.weeklyProgress(10L, "2024-06-03");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(progressStatisticsService);
    }

    @Test
    void testWeeklyProgress_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> progressResolver.weeklyProgress(10L, "2024-06-03"));
    }

    // monthlyStatistics
    @Test
    void testMonthlyStatistics_userNotFound_returnsZeroStats() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(5L)).thenReturn(null);

        MonthlyStatistics stats = progressResolver.monthlyStatistics(5L, 2024, 7);
        assertNotNull(stats);
        assertEquals(2024, stats.year());
        assertEquals(7, stats.month());
        assertEquals(0, stats.totalCompleted());
        assertNotNull(stats.categoryCounts());
        assertTrue(stats.categoryCounts().isEmpty());
    }

    @Test
    void testMonthlyStatistics_happyPath_mapsCounts() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(5L)).thenReturn(current);

        ProgressStatisticsService.CategoryCount c1 = new ProgressStatisticsService.CategoryCount(HabitCategory.PHYSICAL, 3);
        ProgressStatisticsService.CategoryCount c2 = new ProgressStatisticsService.CategoryCount(HabitCategory.SLEEP, 1);
        ProgressStatisticsService.MonthlyStats ms = new ProgressStatisticsService.MonthlyStats(2025, 1, 4, List.of(c1, c2));
        when(progressStatisticsService.monthlyStats(current, 2025, 1)).thenReturn(ms);

        MonthlyStatistics out = progressResolver.monthlyStatistics(5L, 2025, 1);
        assertEquals(2025, out.year());
        assertEquals(1, out.month());
        assertEquals(4, out.totalCompleted());
        assertEquals(2, out.categoryCounts().size());
        assertEquals(HabitCategory.PHYSICAL, out.categoryCounts().get(0).category());
        assertEquals(3, out.categoryCounts().get(0).count());
        assertEquals(HabitCategory.SLEEP, out.categoryCounts().get(1).category());
        assertEquals(1, out.categoryCounts().get(1).count());
    }

    @Test
    void testMonthlyStatistics_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> progressResolver.monthlyStatistics(5L, 2025, 1));
    }

    // logCompletedActivity
    @Test
    void testLogCompletedActivity_createsNewLogWhenNoneExists() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(7L)).thenReturn(current);

        Routine routine = new Routine();
        when(routineService.findById(100L)).thenReturn(routine);

        Habit habit = new Habit(); habit.setCategory(HabitCategory.PHYSICAL);
        when(habitService.findById(200L)).thenReturn(habit);

        when(progressLogService.findByUserAndDate(eq(current), any(LocalDate.class))).thenReturn(List.of());
        // capture ProgressLog saved
        when(progressLogService.create(any(ProgressLog.class))).thenAnswer(inv -> inv.getArgument(0));

        CompletedActivity ca = new CompletedActivity();
        when(completedActivityService.create(any(ProgressLog.class), any(Habit.class), any(LocalDateTime.class), anyString()))
                .thenReturn(ca);

        CompletedActivity result = progressResolver.logCompletedActivity(7L, 100L, 200L, "note");
        assertNotNull(result);

        ArgumentCaptor<ProgressLog> logCaptor = ArgumentCaptor.forClass(ProgressLog.class);
        verify(progressLogService).create(logCaptor.capture());
        ProgressLog saved = logCaptor.getValue();
        assertEquals(current, saved.getUser());
        assertEquals(routine, saved.getRoutine());
        assertEquals(LocalDate.now(), saved.getDate());

        verify(completedActivityService).create(any(ProgressLog.class), eq(habit), any(LocalDateTime.class), eq("note"));
    }

    @Test
    void testLogCompletedActivity_reusesExistingLog() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(7L)).thenReturn(current);

        ProgressLog existing = new ProgressLog();
        existing.setUser(current);
        existing.setDate(LocalDate.now());
        when(progressLogService.findByUserAndDate(eq(current), any(LocalDate.class))).thenReturn(List.of(existing));

        Habit habit = new Habit(); habit.setCategory(HabitCategory.SLEEP);
        when(habitService.findById(200L)).thenReturn(habit);

        CompletedActivity ca = new CompletedActivity();
        when(completedActivityService.create(any(ProgressLog.class), any(Habit.class), any(LocalDateTime.class), anyString()))
                .thenReturn(ca);

        CompletedActivity result = progressResolver.logCompletedActivity(7L, null, 200L, "zzz");
        assertNotNull(result);
        verify(progressLogService, never()).create(any());
        // ensure completedActivityService called with the existing log
        ArgumentCaptor<ProgressLog> logCaptor = ArgumentCaptor.forClass(ProgressLog.class);
        verify(completedActivityService).create(logCaptor.capture(), eq(habit), any(LocalDateTime.class), eq("zzz"));
        assertEquals(existing, logCaptor.getValue());
    }

    @Test
    void testLogCompletedActivity_userNotFound_throws() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(7L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> progressResolver.logCompletedActivity(7L, null, 200L, "n"));
    }

    @Test
    void testLogCompletedActivity_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> progressResolver.logCompletedActivity(7L, null, 200L, "n"));
    }
}
