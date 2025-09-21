package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.service.HabitService;
import org.una.progra3.healthy_life.service.UserService;

import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;

import java.util.List;

@Controller
public class HabitResolver {

    @Autowired
    private HabitService habitService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @QueryMapping
    public List<Habit> habitsByCategory(@Argument HabitCategory category) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return habitService.findByCategory(category);
    }

    @QueryMapping
    // ...existing code...
    public Habit habitById(@Argument Long id) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return habitService.findById(id);
    }

    @QueryMapping
    public List<Habit> userFavoriteHabits(@Argument Long userId) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        User u = userService.findById(userId);
        return u == null || u.getFavoriteHabits() == null ? List.of() : List.copyOf(u.getFavoriteHabits());
    }

    @MutationMapping
    public Habit createHabit(@Argument String name, @Argument HabitCategory category, @Argument String description) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        Habit h = new Habit();
        h.setName(name);
        h.setCategory(category);
        h.setDescription(description);
        return habitService.create(h);
    }

    @MutationMapping
    public Habit updateHabit(@Argument Long id, @Argument String name, @Argument HabitCategory category, @Argument String description) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        return habitService.update(id, name, category, description);
    }

    @MutationMapping
    // ...existing code...
    public Boolean deleteHabit(@Argument Long id) {
        User currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkDelete(currentUser);
        return habitService.deleteById(id);
    }
}
