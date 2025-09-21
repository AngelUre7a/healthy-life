package org.una.progra3.healthy_life.resolver;
import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.*;
import java.time.LocalTime;
import java.util.List;
// ...existing code...

@Controller
public class RoutineResolver {
    public static record RoutineActivityInput(Long habitId, Integer duration, String targetTime, String notes) {}
    public static record CreateRoutineInput(Long userId, String title, String daysOfWeek, java.util.List<RoutineActivityInput> activities) {}

    @Autowired private RoutineService routineService;
    @Autowired private RoutineActivityService routineActivityService;
    @Autowired private HabitService habitService;
    @Autowired private UserService userService;
    @Autowired private AuthenticationService authenticationService;

    @QueryMapping
    public List<Routine> routinesByUser(@Argument Long userId) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);

        User user = userService.findById(userId);
        return user == null ? List.of() : routineService.findByUser(user);
    }

    @QueryMapping
    public Routine routineById(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return routineService.findById(id);
    }

    @MutationMapping
    public Routine createRoutine(@Argument CreateRoutineInput input) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);

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
                if (habit == null) throw new RuntimeException("Habit not found");
                routineActivityService.create(
                        saved,
                        habit,
                        ai.duration(),
                        ai.targetTime() != null ? LocalTime.parse(ai.targetTime()) : null,
                        ai.notes()
                );
            }
        }
        return routineService.findById(saved.getId());
    }

    @MutationMapping
    public Routine updateRoutine(@Argument Long id, @Argument String title, @Argument String daysOfWeek) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        return routineService.update(id, title, daysOfWeek);
    }

    @MutationMapping
    public Boolean deleteRoutine(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkDelete(currentUser);
        return routineService.deleteById(id);
    }

    @MutationMapping
    public RoutineActivity addRoutineActivity(@Argument Long routineId, @Argument Long habitId,
                                              @Argument Integer duration, @Argument String targetTime,
                                              @Argument String notes) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);

        Routine routine = routineService.findById(routineId);
        if (routine == null) throw new RuntimeException("Routine not found");

        Habit habit = habitService.findById(habitId);
        if (habit == null) throw new RuntimeException("Habit not found");

        return routineActivityService.create(
                routine,
                habit,
                duration,
                targetTime != null ? LocalTime.parse(targetTime) : null,
                notes
        );
    }

    @MutationMapping
    public RoutineActivity updateRoutineActivity(@Argument Long id, @Argument Integer duration,
                                                 @Argument String targetTime, @Argument String notes) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);

        return routineActivityService.update(
                id,
                duration,
                targetTime != null ? LocalTime.parse(targetTime) : null,
                notes
        );
    }

    @MutationMapping
    public Boolean deleteRoutineActivity(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkDelete(currentUser);
        return routineActivityService.deleteById(id);
    }
}
