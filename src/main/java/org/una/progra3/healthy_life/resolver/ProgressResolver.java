package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.*;

import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class ProgressResolver {

    @Autowired private ProgressLogService progressLogService;
    @Autowired private CompletedActivityService completedActivityService;
    @Autowired private RoutineService routineService;
    @Autowired private UserService userService;
    @Autowired private HabitService habitService;
    @Autowired private ProgressStatisticsService progressStatisticsService;
    @Autowired private AuthenticationService authenticationService;

    @QueryMapping
    public ProgressLog progressLogByDate(@Argument Long userId, @Argument String date) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        User u = userService.findById(userId);
        if (u == null) return null;
        List<ProgressLog> logs = progressLogService.findByUserAndDate(u, LocalDate.parse(date));
        return logs.isEmpty() ? null : logs.getFirst();
    }

    @QueryMapping
    public List<DailyProgress> weeklyProgress(@Argument Long userId, @Argument String startDate) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        User u = userService.findById(userId);
        if (u == null) return List.of();
        Map<java.time.LocalDate, Long> map = progressStatisticsService.weeklyCompletedCounts(u, LocalDate.parse(startDate));
        return map.entrySet().stream().map(e -> new DailyProgress(e.getKey().toString(), e.getValue().intValue())).toList();
    }

    @QueryMapping
    public MonthlyStatistics monthlyStatistics(@Argument Long userId, @Argument int year, @Argument int month) {
    User currentUser = authenticationService.getCurrentUser();
    PermissionValidator.checkRead(currentUser);
    User u = userService.findById(userId);
    if (u == null) return new MonthlyStatistics(year, month, 0, List.of());
    var stats = progressStatisticsService.monthlyStats(u, year, month);
    return new MonthlyStatistics(stats.year(), stats.month(), stats.totalCompleted(),
        stats.categoryCounts().stream().map(c -> new CategoryCount(c.category(), c.count())).toList());
    }

    @MutationMapping
    public CompletedActivity logCompletedActivity(@Argument Long userId, @Argument Long routineId,
                                                  @Argument Long habitId, @Argument String notes) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        User u = userService.findById(userId);
        if (u == null) throw new RuntimeException("User not found");
        Routine r = routineId != null ? routineService.findById(routineId) : null;
        Habit h = habitService.findById(habitId);
        LocalDate today = LocalDate.now();
        List<ProgressLog> todayLogs = progressLogService.findByUserAndDate(u, today);
        ProgressLog log;
        if (todayLogs.isEmpty()) {
            log = new ProgressLog();
            log.setUser(u);
            log.setRoutine(r);
            log.setDate(today);
            log = progressLogService.create(log);
        } else {
            log = todayLogs.getFirst();
        }
        return completedActivityService.create(log, h, LocalDateTime.now(), notes);
    }
}

record DailyProgress(String date, int completedCount) {}
record CategoryCount(org.una.progra3.healthy_life.entity.enums.HabitCategory category, int count) {}
record MonthlyStatistics(int year, int month, int totalCompleted, java.util.List<CategoryCount> categoryCounts) {}
