package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.RoutineActivity;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.service.RoutineService;
import org.una.progra3.healthy_life.service.RoutineActivityService;
import org.una.progra3.healthy_life.service.HabitService;
import org.una.progra3.healthy_life.service.UserService;
import org.una.progra3.healthy_life.service.AuthenticationService;
import java.time.LocalTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoutineResolverTest {
    @Mock RoutineService routineService;
    @Mock RoutineActivityService routineActivityService;
    @Mock HabitService habitService;
    @Mock UserService userService;
    @Mock AuthenticationService authenticationService;
    @InjectMocks RoutineResolver routineResolver;

    @Test
    void testRoutineById() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineService.findById(1L)).thenReturn(new Routine());
        Routine result = routineResolver.routineById(1L);
        assertNotNull(result);
    }

    @Test
    void testRoutinesByUser() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(routineService.findByUser(user)).thenReturn(List.of(new Routine()));
        List<Routine> result = routineResolver.routinesByUserSimple(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateRoutine() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Routine routine = new Routine();
        routine.setId(10L);
        routine.setUser(user);
        routine.setTitle("Rutina");
        routine.setDaysOfWeek("L,M,X");
        Mockito.when(routineService.create(Mockito.any())).thenReturn(routine);
        Mockito.when(routineService.findById(10L)).thenReturn(routine);
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina", "L,M,X", null);
        Routine result = routineResolver.createRoutine(input);
        assertNotNull(result);
        assertEquals("Rutina", result.getTitle());
    }

    @Test
    void testCreateRoutineWithoutWritePermission() {
        Role role = new Role();
        role.setCanWrite(false);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina", "L,M,X", null);
        assertThrows(RuntimeException.class, () -> routineResolver.createRoutine(input));
    }

    @Test
    void testCreateRoutineWithNullUser() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(null);
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina", "L,M,X", null);
        assertThrows(RuntimeException.class, () -> routineResolver.createRoutine(input));
    }

    @Test
    void testCreateRoutineWithNullInput() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        assertThrows(RuntimeException.class, () -> routineResolver.createRoutine(null));
    }

    @Test
    void testCreateRoutineWithActivitiesSuccess() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        Routine saved = new Routine();
        saved.setId(99L);
        saved.setUser(user);
        saved.setTitle("Rutina con act");
        saved.setDaysOfWeek("L,M");

        Mockito.when(routineService.create(any(Routine.class))).thenReturn(saved);
        Mockito.when(routineService.findById(99L)).thenReturn(saved);

        Habit habit = new Habit();
        habit.setId(5L);
        Mockito.when(habitService.findById(5L)).thenReturn(habit);

        var activities = List.of(new RoutineResolver.RoutineActivityInput(5L, 25, "08:15", "Notas act"));
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina con act", "L,M", activities);

        Routine result = routineResolver.createRoutine(input);
        assertNotNull(result);
        assertEquals(99L, result.getId());

        Mockito.verify(routineActivityService).create(
                eq(saved),
                eq(habit),
                eq(25),
                eq(LocalTime.parse("08:15")),
                eq("Notas act")
        );
    }

    @Test
    void testCreateRoutineWithActivitiesNullTime() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        Routine saved = new Routine();
        saved.setId(77L);
        saved.setUser(user);
        saved.setTitle("Rutina null time");
        saved.setDaysOfWeek("X,J");

        Mockito.when(routineService.create(any(Routine.class))).thenReturn(saved);
        Mockito.when(routineService.findById(77L)).thenReturn(saved);

        Habit habit = new Habit();
        habit.setId(6L);
        Mockito.when(habitService.findById(6L)).thenReturn(habit);

        var activities = List.of(new RoutineResolver.RoutineActivityInput(6L, 15, null, "Sin hora"));
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina null time", "X,J", activities);

        Routine result = routineResolver.createRoutine(input);
        assertNotNull(result);

        Mockito.verify(routineActivityService).create(
                eq(saved),
                eq(habit),
                eq(15),
                isNull(),
                eq("Sin hora")
        );
    }

    @Test
    void testCreateRoutineWithEmptyActivitiesList() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        Routine saved = new Routine();
        saved.setId(66L);
        saved.setUser(user);
        saved.setTitle("Rutina sin acts");
        saved.setDaysOfWeek("L");

        Mockito.when(routineService.create(any(Routine.class))).thenReturn(saved);
        Mockito.when(routineService.findById(66L)).thenReturn(saved);

        var activities = List.<RoutineResolver.RoutineActivityInput>of();
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina sin acts", "L", activities);

        Routine result = routineResolver.createRoutine(input);
        assertNotNull(result);
        Mockito.verifyNoInteractions(routineActivityService);
    }

    @Test
    void testCreateRoutineHabitNotFound() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(userService.findById(1L)).thenReturn(user);

        Routine saved = new Routine();
        saved.setId(55L);
        saved.setUser(user);
        saved.setTitle("Rutina error hábito");
        saved.setDaysOfWeek("V");

        Mockito.when(routineService.create(any(Routine.class))).thenReturn(saved);

        // Habit no encontrado
        Mockito.when(habitService.findById(999L)).thenReturn(null);

        var activities = List.of(new RoutineResolver.RoutineActivityInput(999L, 10, "07:00", "x"));
        RoutineResolver.CreateRoutineInput input = new RoutineResolver.CreateRoutineInput(1L, "Rutina error hábito", "V", activities);

        assertThrows(RuntimeException.class, () -> routineResolver.createRoutine(input));

        // No se debe intentar crear actividades ni buscar la rutina final
        Mockito.verify(routineActivityService, Mockito.never()).create(any(), any(), any(), any(), any());
        Mockito.verify(routineService, Mockito.never()).findById(anyLong());
    }

    @Test
    void testAddRoutineActivity() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Routine routine = new Routine();
        routine.setId(10L);
        Mockito.when(routineService.findById(10L)).thenReturn(routine);
        Habit habit = new Habit();
        habit.setId(5L);
        Mockito.when(habitService.findById(5L)).thenReturn(habit);
        RoutineActivity activity = new RoutineActivity();
        Mockito.when(routineActivityService.create(routine, habit, 30, LocalTime.parse("08:00"), "Notas")).thenReturn(activity);
        RoutineActivity result = routineResolver.addRoutineActivity(10L, 5L, 30, "08:00", "Notas");
        assertNotNull(result);
    }

    @Test
    void testAddRoutineActivityWithoutWritePermission() {
        Role role = new Role();
        role.setCanWrite(false);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        assertThrows(RuntimeException.class, () -> routineResolver.addRoutineActivity(10L, 5L, 30, "08:00", "Notas"));
    }

    @Test
    void testAddRoutineActivityWithNullRoutine() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineService.findById(10L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> routineResolver.addRoutineActivity(10L, 5L, 30, "08:00", "Notas"));
    }

    @Test
    void testAddRoutineActivityWithNullHabit() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineService.findById(10L)).thenReturn(new Routine());
        Mockito.when(habitService.findById(5L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> routineResolver.addRoutineActivity(10L, 5L, 30, "08:00", "Notas"));
    }

    @Test
    void testAddRoutineActivityWithInvalidDuration() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineService.findById(10L)).thenReturn(new Routine());
        Mockito.when(habitService.findById(5L)).thenReturn(new Habit());
        assertThrows(RuntimeException.class, () -> routineResolver.addRoutineActivity(10L, 5L, -5, "08:00", "Notas"));
    }

    @Test
    void testUpdateRoutineActivity() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        RoutineActivity activity = new RoutineActivity();
        Mockito.when(routineActivityService.update(1L, 45, LocalTime.parse("09:00"), "Actualizado")).thenReturn(activity);
        RoutineActivity result = routineResolver.updateRoutineActivity(1L, 45, "09:00", "Actualizado");
        assertNotNull(result);
    }

    @Test
    void testUpdateRoutine() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Routine routine = new Routine();
        Mockito.when(routineService.update(1L, "Nuevo título", "L,V")).thenReturn(routine);
        Routine result = routineResolver.updateRoutine(1L, "Nuevo título", "L,V");
        assertNotNull(result);
    }

    @Test
    void testDeleteRoutine() {
        Role role = new Role();
        role.setCanDelete(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineService.deleteById(1L)).thenReturn(true);
        Boolean result = routineResolver.deleteRoutine(1L);
        assertTrue(result);
    }

    @Test
    void testDeleteRoutineActivity() {
        Role role = new Role();
        role.setCanDelete(true);
        User user = new User();
        user.setRole(role);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(user);
        Mockito.when(routineActivityService.deleteById(1L)).thenReturn(true);
        Boolean result = routineResolver.deleteRoutineActivity(1L);
        assertTrue(result);
    }
}
