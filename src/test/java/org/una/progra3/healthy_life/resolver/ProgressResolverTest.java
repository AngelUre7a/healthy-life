package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.*;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressResolverTest {

    @Mock ProgressLogService progressLogService;
    @Mock CompletedActivityService completedActivityService;
    @Mock RoutineService routineService;
    @Mock UserService userService;
    @Mock HabitService habitService;
    @Mock ProgressStatisticsService progressStatisticsService;
    @Mock AuthenticationService authenticationService;

    @InjectMocks ProgressResolver resolver;

    private User userWithRead() {
        Role role = new Role(); role.setCanRead(true); User u = new User(); u.setRole(role); return u;
    }

    private User userWithWrite() {
        Role role = new Role(); role.setCanWrite(true); User u = new User(); u.setRole(role); return u;
    }

    @Test
    void progressLogByDate_returnsFirstOrNull() {
        when(authenticationService.getCurrentUser()).thenReturn(userWithRead());
        User u = new User(); u.setId(1L);
        when(userService.findById(1L)).thenReturn(u);
        ProgressLog pl1 = new ProgressLog(); pl1.setId(10L);
        ProgressLog pl2 = new ProgressLog(); pl2.setId(11L);
        when(progressLogService.findByUserAndDate(eq(u), any(LocalDate.class))).thenReturn(List.of(pl1, pl2));

        ProgressLog got = resolver.progressLogByDate(1L, LocalDate.now().toString());
        assertEquals(10L, got.getId());

        when(progressLogService.findByUserAndDate(eq(u), any(LocalDate.class))).thenReturn(List.of());
        assertNull(resolver.progressLogByDate(1L, LocalDate.now().toString()));

        when(userService.findById(2L)).thenReturn(null);
        assertNull(resolver.progressLogByDate(2L, LocalDate.now().toString()));
    }

    @Test
    void weeklyProgress_mapsMapToRecords() {
        when(authenticationService.getCurrentUser()).thenReturn(userWithRead());
        User u = new User(); u.setId(2L);
        when(userService.findById(2L)).thenReturn(u);
        LocalDate d = LocalDate.now();
        when(progressStatisticsService.weeklyCompletedCounts(eq(u), any(LocalDate.class)))
            .thenReturn(Map.of(d, 3L));

        var list = resolver.weeklyProgress(2L, d.toString());
        assertEquals(1, list.size());
        assertEquals(d.toString(), list.getFirst().date());
        assertEquals(3, list.getFirst().completedCount());

        when(userService.findById(3L)).thenReturn(null);
        assertTrue(resolver.weeklyProgress(3L, d.toString()).isEmpty());
    }

    @Test
    void monthlyStatistics_mapsStats() {
        when(authenticationService.getCurrentUser()).thenReturn(userWithRead());
        User u = new User(); u.setId(3L);
        when(userService.findById(3L)).thenReturn(u);

        ProgressStatisticsService.CategoryCount cc = new ProgressStatisticsService.CategoryCount(HabitCategory.PHYSICAL, 5);
        ProgressStatisticsService.MonthlyStats stats = new ProgressStatisticsService.MonthlyStats(2025, 9, 10, List.of(cc));
        when(progressStatisticsService.monthlyStats(eq(u), eq(2025), eq(9))).thenReturn(stats);

        var res = resolver.monthlyStatistics(3L, 2025, 9);
        assertEquals(2025, res.year());
        assertEquals(9, res.month());
        assertEquals(10, res.totalCompleted());
        assertEquals(1, res.categoryCounts().size());

        when(userService.findById(4L)).thenReturn(null);
        var empty = resolver.monthlyStatistics(4L, 2025, 9);
        assertEquals(0, empty.totalCompleted());
        assertTrue(empty.categoryCounts().isEmpty());
    }

    @Test
    void logCompletedActivity_createsLogIfMissing_andCallsService() {
        when(authenticationService.getCurrentUser()).thenReturn(userWithWrite());
        User u = new User(); u.setId(10L);
        when(userService.findById(10L)).thenReturn(u);
        when(progressLogService.findByUserAndDate(eq(u), any(LocalDate.class))).thenReturn(List.of());
        ProgressLog created = new ProgressLog(); created.setId(99L);
        when(progressLogService.create(any(ProgressLog.class))).thenReturn(created);
        Routine r = new Routine(); r.setId(7L);
        when(routineService.findById(7L)).thenReturn(r);
        Habit h = new Habit(); h.setId(8L);
        when(habitService.findById(8L)).thenReturn(h);
        CompletedActivity ca = new CompletedActivity(); ca.setId(55L);
        when(completedActivityService.create(any(ProgressLog.class), eq(h), any(LocalDateTime.class), eq("notes")))
            .thenReturn(ca);

        CompletedActivity out = resolver.logCompletedActivity(10L, 7L, 8L, "notes");
        assertEquals(55L, out.getId());
        verify(progressLogService).create(any(ProgressLog.class));
        verify(completedActivityService).create(any(ProgressLog.class), eq(h), any(LocalDateTime.class), eq("notes"));
    }

    @Test
    void logCompletedActivity_usesExistingLog_andThrowsWhenUserMissing() {
        when(authenticationService.getCurrentUser()).thenReturn(userWithWrite());
        User u = new User(); u.setId(10L);
        when(userService.findById(10L)).thenReturn(u);
        ProgressLog existing = new ProgressLog(); existing.setId(77L);
        when(progressLogService.findByUserAndDate(eq(u), any(LocalDate.class))).thenReturn(List.of(existing));
        Habit h = new Habit(); h.setId(8L);
        when(habitService.findById(8L)).thenReturn(h);
        when(completedActivityService.create(eq(existing), eq(h), any(LocalDateTime.class), eq("n")))
            .thenReturn(new CompletedActivity());

        assertNotNull(resolver.logCompletedActivity(10L, null, 8L, "n"));

        when(userService.findById(99L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> resolver.logCompletedActivity(99L, null, 8L, "n"));
    }
}
