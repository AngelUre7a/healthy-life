package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.*;

import java.time.LocalTime;
import java.util.List;

record RoutineActivityInput(Long habitId, Integer duration, String targetTime, String notes) {}
record CreateRoutineInput(Long userId, String title, String daysOfWeek, java.util.List<RoutineActivityInput> activities) {}

@Controller
public class RoutineResolver {

    @Autowired private RoutineService routineService;
    @Autowired private RoutineActivityService routineActivityService;
    @Autowired private HabitService habitService;
    @Autowired private UserService userService;

    @QueryMapping
    public List<Routine> routinesByUser(@Argument Long userId) {
        User user = userService.findById(userId);
        return user == null ? List.of() : routineService.findByUser(user);
    }

    @QueryMapping
    public Routine routineById(@Argument Long id) { return routineService.findById(id); }

    @MutationMapping
    public Routine createRoutine(@Argument CreateRoutineInput input) {
        User user = userService.findById(input.userId());
        if (user == null) throw new RuntimeException("User not found");
        Routine routine = new Routine();
        routine.setUser(user);
        routine.setTitle(input.title());
        routine.setDaysOfWeek(input.daysOfWeek());
        Routine saved = routineService.create(routine);
        if (input.activities() != null) {
            for (RoutineActivityInput ai : input.activities()) {
                Habit habit = habitService.findById(ai.habitId());
                routineActivityService.create(saved, habit, ai.duration(),
                        ai.targetTime() != null ? LocalTime.parse(ai.targetTime()) : null,
                        ai.notes());
            }
        }
        return routineService.findById(saved.getId());
    }

    @MutationMapping
    public Routine updateRoutine(@Argument Long id, @Argument String title, @Argument String daysOfWeek) {
        return routineService.update(id, title, daysOfWeek);
    }

    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) { return routineService.deleteById(id); }

    @MutationMapping
    public RoutineActivity addRoutineActivity(@Argument Long routineId, @Argument Long habitId,
                                              @Argument Integer duration, @Argument String targetTime,
                                              @Argument String notes) {
        Routine routine = routineService.findById(routineId);
        Habit habit = habitService.findById(habitId);
        return routineActivityService.create(routine, habit, duration,
                targetTime != null ? LocalTime.parse(targetTime) : null, notes);
    }

    @MutationMapping
    public RoutineActivity updateRoutineActivity(@Argument Long id, @Argument Integer duration,
                                                 @Argument String targetTime, @Argument String notes) {
        return routineActivityService.update(id, duration,
                targetTime != null ? LocalTime.parse(targetTime) : null, notes);
    }

    @MutationMapping
    public Boolean deleteRoutineActivity(@Argument Long id) { return routineActivityService.deleteById(id); }
}
