package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.CompletedActivity;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.ProgressLogRepository;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ProgressStatisticsServiceTest {
    @Test
    void testWeeklyCompletedCountsEmpty() {
        User user = new User();
        LocalDate start = LocalDate.of(2025, 9, 15);
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(Collections.emptyList());
        Map<LocalDate, Long> result = progressStatisticsService.weeklyCompletedCounts(user, start);
        assertEquals(7, result.size());
        assertTrue(result.values().stream().allMatch(v -> v == 0L));
    }

    @Test
    void testMonthlyStatsNoLogs() {
        User user = new User();
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(Collections.emptyList());
        ProgressStatisticsService.MonthlyStats stats = progressStatisticsService.monthlyStats(user, 2025, 9);
        assertEquals(2025, stats.year());
        assertEquals(9, stats.month());
        assertEquals(0, stats.totalCompleted());
        assertTrue(stats.categoryCounts().stream().allMatch(cc -> cc.count() == 0));
    }

    @Test
    void testMonthlyStatsNullHabitCategory() {
        User user = new User();
        ProgressLog log = new ProgressLog();
        log.setDate(LocalDate.of(2025, 9, 20));
        CompletedActivity ca = new CompletedActivity();
        ca.setHabit(null); // Habit es null
        log.setCompletedActivities(List.of(ca));
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(log));
        ProgressStatisticsService.MonthlyStats stats = progressStatisticsService.monthlyStats(user, 2025, 9);
        assertEquals(1, stats.totalCompleted());
        assertTrue(stats.categoryCounts().stream().allMatch(cc -> cc.count() == 0 || cc.count() == 1));
    }
    @Mock
    ProgressLogRepository progressLogRepository;
    @InjectMocks
    ProgressStatisticsService progressStatisticsService;

    @Test
    void testWeeklyCompletedCounts() {
        User user = new User();
        LocalDate start = LocalDate.of(2025, 9, 15);
        ProgressLog log = new ProgressLog();
        log.setDate(start.plusDays(1));
        CompletedActivity ca = new CompletedActivity();
        log.setCompletedActivities(List.of(ca, ca));
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(log));
        Map<LocalDate, Long> result = progressStatisticsService.weeklyCompletedCounts(user, start);
        assertEquals(7, result.size());
        assertEquals(2L, result.get(start.plusDays(1)));
    }

    @Test
    void testWeeklyCompletedCounts_OutOfRangeLogsIgnored() {
        User user = new User();
        LocalDate start = LocalDate.of(2025, 9, 15);
        LocalDate end = start.plusDays(6);

        ProgressLog before = new ProgressLog();
        before.setDate(start.minusDays(1));
        before.setCompletedActivities(List.of(new CompletedActivity()));

        ProgressLog inside = new ProgressLog();
        inside.setDate(start.plusDays(3));
        inside.setCompletedActivities(List.of(new CompletedActivity(), new CompletedActivity()));

        ProgressLog after = new ProgressLog();
        after.setDate(end.plusDays(1));
        after.setCompletedActivities(List.of(new CompletedActivity()));

        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(before, inside, after));
        Map<LocalDate, Long> result = progressStatisticsService.weeklyCompletedCounts(user, start);
        assertEquals(2L, result.get(start.plusDays(3)));
        // Asegura que no sumó antes/after
        assertTrue(result.entrySet().stream()
                .filter(e -> !e.getKey().equals(start.plusDays(3)))
                .allMatch(e -> e.getValue() == 0L));
    }

    @Test
    void testWeeklyCompletedCounts_NullCompletedActivitiesCountsAsZero() {
        User user = new User();
        LocalDate start = LocalDate.of(2025, 9, 15);
        ProgressLog log = new ProgressLog();
        log.setDate(start.plusDays(2));
        log.setCompletedActivities(null); // null debe contar 0
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(log));
        Map<LocalDate, Long> result = progressStatisticsService.weeklyCompletedCounts(user, start);
        assertEquals(0L, result.get(start.plusDays(2)));
    }

    @Test
    void testMonthlyStats() {
        User user = new User();
        ProgressLog log = new ProgressLog();
        log.setDate(LocalDate.of(2025, 9, 20));
        Habit habit = new Habit();
        habit.setCategory(HabitCategory.PHYSICAL);
        CompletedActivity ca = new CompletedActivity();
        ca.setHabit(habit);
        log.setCompletedActivities(List.of(ca));
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(log));
        ProgressStatisticsService.MonthlyStats stats = progressStatisticsService.monthlyStats(user, 2025, 9);
        assertEquals(2025, stats.year());
        assertEquals(9, stats.month());
        assertEquals(1, stats.totalCompleted());
        assertTrue(stats.categoryCounts().stream().anyMatch(cc -> cc.category() == HabitCategory.PHYSICAL && cc.count() == 1));
    }

    @Test
    void testMonthlyStats_IgnoreOutOfMonthLogs() {
        User user = new User();
        // Log el último día del mes anterior y el primero del mes siguiente
        ProgressLog prev = new ProgressLog();
        prev.setDate(LocalDate.of(2025, 8, 31));
        prev.setCompletedActivities(List.of(new CompletedActivity()));

        ProgressLog next = new ProgressLog();
        next.setDate(LocalDate.of(2025, 10, 1));
        next.setCompletedActivities(List.of(new CompletedActivity()));

        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(prev, next));
        ProgressStatisticsService.MonthlyStats stats = progressStatisticsService.monthlyStats(user, 2025, 9);
        assertEquals(0, stats.totalCompleted());
    }

    @Test
    void testMonthlyStats_NullCompletedActivitiesDoesNotIncreaseTotals() {
        User user = new User();
        ProgressLog log = new ProgressLog();
        log.setDate(LocalDate.of(2025, 9, 10));
        log.setCompletedActivities(null);
        Mockito.when(progressLogRepository.findByUser(user)).thenReturn(List.of(log));
        ProgressStatisticsService.MonthlyStats stats = progressStatisticsService.monthlyStats(user, 2025, 9);
        assertEquals(0, stats.totalCompleted());
        assertTrue(stats.categoryCounts().stream().allMatch(cc -> cc.count() == 0));
    }
}
