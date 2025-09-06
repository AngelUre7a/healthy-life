package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.ProgressLogRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProgressStatisticsService {

    @Autowired
    private ProgressLogRepository progressLogRepository;

    public Map<LocalDate, Long> weeklyCompletedCounts(User user, LocalDate startDate) {
        LocalDate end = startDate.plusDays(6);
        List<ProgressLog> logs = progressLogRepository.findByUser(user).stream()
                .filter(pl -> !pl.getDate().isBefore(startDate) && !pl.getDate().isAfter(end))
                .toList();
        Map<LocalDate, Long> map = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) map.put(startDate.plusDays(i), 0L);
        for (ProgressLog pl : logs) {
            long count = pl.getCompletedActivities() == null ? 0 : pl.getCompletedActivities().size();
            map.computeIfPresent(pl.getDate(), (d, old) -> old + count);
        }
        return map;
    }

    public MonthlyStats monthlyStats(User user, int year, int month) {
        LocalDate first = LocalDate.of(year, month, 1);
        LocalDate last = first.plusMonths(1).minusDays(1);
        List<ProgressLog> logs = progressLogRepository.findByUser(user).stream()
                .filter(pl -> !pl.getDate().isBefore(first) && !pl.getDate().isAfter(last))
                .toList();
        int total = 0;
        Map<HabitCategory, Integer> byCat = new EnumMap<>(HabitCategory.class);
        for (HabitCategory c : HabitCategory.values()) byCat.put(c, 0);
        for (ProgressLog pl : logs) {
            if (pl.getCompletedActivities() != null) {
                total += pl.getCompletedActivities().size();
                pl.getCompletedActivities().forEach(ca -> {
                    HabitCategory cat = ca.getHabit() != null ? ca.getHabit().getCategory() : null;
                    if (cat != null) byCat.put(cat, byCat.get(cat) + 1);
                });
            }
        }
        List<CategoryCount> categoryCounts = byCat.entrySet().stream()
                .map(e -> new CategoryCount(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new MonthlyStats(year, month, total, categoryCounts);
    }

    public record DailyProgressDTO(LocalDate date, long completedCount) {}
    public record CategoryCount(HabitCategory category, int count) {}
    public record MonthlyStats(int year, int month, int totalCompleted, List<CategoryCount> categoryCounts) {}
}
