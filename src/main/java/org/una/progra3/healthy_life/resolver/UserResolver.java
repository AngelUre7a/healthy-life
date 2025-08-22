package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.dtos.UserDTO;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserResolver {

    @Autowired
    private UserService userService;

    private UserDTO toDTO(User user) {
    if (user == null) return null;
    UserDTO dto = new UserDTO();
    if (user.getId() != null) dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    dto.setRoleId(user.getRole() != null ? user.getRole().getId() : null);
    Set<Long> favIds = user.getFavoriteHabits() == null ? Set.of() :
        user.getFavoriteHabits().stream().map(Habit::getId).collect(Collectors.toSet());
    dto.setFavoriteHabitIds(favIds);
    dto.setRoutineIds(user.getRoutines() == null ? List.of() :
        user.getRoutines().stream().map(Routine::getId).collect(Collectors.toList()));
    dto.setProgressLogIds(user.getProgressLogs() == null ? List.of() :
        user.getProgressLogs().stream().map(ProgressLog::getId).collect(Collectors.toList()));
    dto.setReminderIds(user.getReminders() == null ? List.of() :
        user.getReminders().stream().map(Reminder::getId).collect(Collectors.toList()));
    dto.setAuthTokenIds(user.getAuthTokens() == null ? List.of() :
        user.getAuthTokens().stream().map(AuthToken::getId).collect(Collectors.toList()));
    return dto;
    }

    @QueryMapping
    public List<UserDTO> allUsers() {
        return userService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @QueryMapping
    public UserDTO userById(@Argument Long id) {
        return toDTO(userService.findById(id));
    }

    @QueryMapping
    public UserDTO userByEmail(@Argument String email) {
        return toDTO(userService.findByEmail(email));
    }


    @MutationMapping
    public UserDTO createUser(@Argument String name, @Argument String email,
                              @Argument String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        return toDTO(userService.save(user));
    }

    @MutationMapping
    public UserDTO updateUser(@Argument Long id, @Argument String name,
                              @Argument String email, @Argument String password) {
        return toDTO(userService.update(id, name, email, password));
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userService.deleteById(id);
    }
}
