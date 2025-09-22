package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.una.progra3.healthy_life.dtos.UserDTO;
import org.una.progra3.healthy_life.dtos.PageInputDTO;
import org.una.progra3.healthy_life.dtos.UserPagedResponseDTO;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.UserService;

import graphql.schema.DataFetchingEnvironment;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.service.HabitService;

import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserResolver {

    @Autowired
    private UserService userService;
  
    @Autowired
    private HabitService habitService;
    @Autowired
    private AuthenticationService authenticationService;

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        if (user.getId() != null) dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoleId(user.getRole() != null ? user.getRole().getId() : null);
        
        // Handle collections safely to avoid ConcurrentModificationException
        try {
            Set<Long> favIds = Set.of();
            if (user.getFavoriteHabits() != null) {
                favIds = user.getFavoriteHabits().stream()
                    .map(Habit::getId)
                    .collect(Collectors.toSet());
            }
            dto.setFavoriteHabitIds(favIds);
        } catch (Exception e) {
            dto.setFavoriteHabitIds(Set.of());
        }
        
        try {
            List<Long> routineIds = List.of();
            if (user.getRoutines() != null) {
                routineIds = user.getRoutines().stream()
                    .map(Routine::getId)
                    .collect(Collectors.toList());
            }
            dto.setRoutineIds(routineIds);
        } catch (Exception e) {
            dto.setRoutineIds(List.of());
        }
        
        try {
            List<Long> progressIds = List.of();
            if (user.getProgressLogs() != null) {
                progressIds = user.getProgressLogs().stream()
                    .map(ProgressLog::getId)
                    .collect(Collectors.toList());
            }
            dto.setProgressLogIds(progressIds);
        } catch (Exception e) {
            dto.setProgressLogIds(List.of());
        }
        
        try {
            List<Long> reminderIds = List.of();
            if (user.getReminders() != null) {
                reminderIds = user.getReminders().stream()
                    .map(Reminder::getId)
                    .collect(Collectors.toList());
            }
            dto.setReminderIds(reminderIds);
        } catch (Exception e) {
            dto.setReminderIds(List.of());
        }
        
        try {
            List<Long> tokenIds = List.of();
            if (user.getAuthTokens() != null) {
                tokenIds = user.getAuthTokens().stream()
                    .map(AuthToken::getId)
                    .collect(Collectors.toList());
            }
            dto.setAuthTokenIds(tokenIds);
        } catch (Exception e) {
            dto.setAuthTokenIds(List.of());
        }
        
        return dto;
    }

    @QueryMapping
    public List<UserDTO> allUsersSimple() {
        return userService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @QueryMapping
    @Transactional(readOnly = true)
    public UserPagedResponseDTO allUsers(@Argument PageInputDTO pageInput) {
        if (pageInput == null) {
            pageInput = new PageInputDTO(0, 20);
        }
        
        Page<User> userPage = userService.findAllPaginated(pageInput);
        List<UserDTO> userDTOs = userPage.getContent().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        return new UserPagedResponseDTO(userDTOs, userService.createPageInfo(userPage));
    }

    @QueryMapping
    public UserDTO userById(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return toDTO(userService.findById(id));
    }

    @QueryMapping
    public UserDTO userByEmail(@Argument String email) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkRead(currentUser);
        return toDTO(userService.findByEmail(email));
    }

    @QueryMapping
    public UserDTO currentUser(DataFetchingEnvironment env) {
        // Retorna el usuario autenticado utilizando el JWT del header Authorization
        var user = authenticationService.getCurrentUser();
        return toDTO(user);
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
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        return toDTO(userService.update(id, name, email, password));
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkDelete(currentUser);
        return userService.deleteById(id);
    }

    @MutationMapping
    public UserDTO toggleFavoriteHabit(@Argument Long userId, @Argument Long habitId) {
        var currentUser = authenticationService.getCurrentUser();
        PermissionValidator.checkWrite(currentUser);
        Habit habit = habitService.findById(habitId);
        User updated = userService.toggleFavorite(userId, habit);
        return toDTO(updated);
    }
}
