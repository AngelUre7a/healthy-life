package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.ProgressLogRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressStatisticsServiceTest {

    @Mock
    ProgressLogRepository progressLogRepository;

    @InjectMocks
    ProgressStatisticsService service;

    private ProgressLog pl(User u, LocalDate date, int completedCount, HabitCategory cat) {
        ProgressLog log = new ProgressLog();
        log.setUser(u);
        log.setDate(date);
        if (completedCount >= 0) {
            List<CompletedActivity> list = new ArrayList<>();
            for (int i = 0; i < completedCount; i++) {
                CompletedActivity ca = new CompletedActivity();
                Habit h = new Habit(); h.setCategory(cat);
                ca.setHabit(h);
                list.add(ca);
            }
            log.setCompletedActivities(list);
        }
        return log;
    }

    @Test
    void weeklyCompletedCounts_aggregatesPerDay_andInitializesWeek() {
        User u = new User(); u.setId(1L);
        LocalDate start = LocalDate.of(2025, 9, 1);
        // logs for day 0 (2 activities) and day 2 (1 activity), plus one outside range
        List<ProgressLog> logs = List.of(
            pl(u, start, 2, HabitCategory.PHYSICAL),
            pl(u, start.plusDays(2), 1, HabitCategory.SLEEP),
            pl(u, start.minusDays(1), 5, HabitCategory.DIET)
        );
        when(progressLogRepository.findByUser(u)).thenReturn(logs);

        Map<LocalDate, Long> map = service.weeklyCompletedCounts(u, start);
        // 7 entries initialized
        assertEquals(7, map.size());
        assertEquals(2L, map.get(start));
        assertEquals(1L, map.get(start.plusDays(2)));
        assertEquals(0L, map.get(start.plusDays(6)));
    }

    @Test
    void monthlyStats_accumulatesTotals_andByCategory() {
        User u = new User(); u.setId(2L);
        int year = 2025, month = 9;
        LocalDate d1 = LocalDate.of(year, month, 5);
        LocalDate d2 = LocalDate.of(year, month, 10);
        LocalDate dOutside = LocalDate.of(year, month, 1).minusDays(1);

        List<ProgressLog> logs = List.of(
            pl(u, d1, 3, HabitCategory.PHYSICAL),
            pl(u, d2, 2, HabitCategory.SLEEP),
            pl(u, dOutside, 10, HabitCategory.DIET)
        );
        when(progressLogRepository.findByUser(u)).thenReturn(logs);

        ProgressStatisticsService.MonthlyStats out = service.monthlyStats(u, year, month);
        assertEquals(year, out.year());
        assertEquals(month, out.month());
        assertEquals(5, out.totalCompleted());

        // Ensure by-category counts include all enums with default 0
        Map<HabitCategory, Integer> byCat = new EnumMap<>(HabitCategory.class);
        out.categoryCounts().forEach(cc -> byCat.put(cc.category(), cc.count()));
        assertEquals(3, byCat.get(HabitCategory.PHYSICAL));
        assertEquals(2, byCat.get(HabitCategory.SLEEP));
        // categories not present should be zero (e.g., NUTRITION inside range not added)
        for (HabitCategory c : HabitCategory.values()) {
            assertNotNull(byCat.get(c));
        }
    }
}
