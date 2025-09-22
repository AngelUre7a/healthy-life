package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.service.HabitService;
import org.una.progra3.healthy_life.service.UserService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HabitResolverTest {
    @Mock HabitService habitService;
    @Mock UserService userService;
    @Mock AuthenticationService authenticationService;
    @InjectMocks HabitResolver habitResolver;

    // queries
    @Test
    void testHabitsByCategory_specificCategory() {
        Role role = new Role(); role.setCanRead(true);
        User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(habitService.findByCategory(HabitCategory.PHYSICAL)).thenReturn(List.of(new Habit()));
        List<Habit> result = habitResolver.habitsByCategorySimple(HabitCategory.PHYSICAL);
        assertEquals(1, result.size());
        verify(habitService).findByCategory(HabitCategory.PHYSICAL);
    }

    @Test
    void testHabitsByCategory_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        assertThrows(RuntimeException.class, () -> habitResolver.habitsByCategorySimple(HabitCategory.SLEEP));
        verify(habitService, never()).findByCategory(any());
    }

    @Test
    void testHabitById_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(habitService.findById(1L)).thenReturn(new Habit());
        Habit result = habitResolver.habitById(1L);
        assertNotNull(result);
        verify(habitService).findById(1L);
    }

    @Test
    void testHabitById_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        assertThrows(RuntimeException.class, () -> habitResolver.habitById(99L));
        verify(habitService, never()).findById(anyLong());
    }

    @Test
    void testUserFavoriteHabits_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        User target = new User();
        Habit h1 = new Habit(); h1.setId(1L); h1.setName("H1");
        Habit h2 = new Habit(); h2.setId(2L); h2.setName("H2");
        target.setFavoriteHabits(Set.of(h1, h2));
        when(userService.findById(10L)).thenReturn(target);
        List<Habit> list = habitResolver.userFavoriteHabits(10L);
        assertEquals(2, list.size());
    }

    @Test
    void testUserFavoriteHabits_userNotFound_returnsEmpty() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.findById(10L)).thenReturn(null);
        List<Habit> list = habitResolver.userFavoriteHabits(10L);
        assertTrue(list.isEmpty());
    }

    @Test
    void testUserFavoriteHabits_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> habitResolver.userFavoriteHabits(10L));
        verifyNoInteractions(userService);
    }

    // mutations
    @Test
    void testCreateHabit_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        Habit created = new Habit();
        when(habitService.create(any(Habit.class))).thenReturn(created);
        Habit out = habitResolver.createHabit("Water", HabitCategory.DIET, "drink water");
        assertNotNull(out);
        verify(habitService).create(any(Habit.class));
    }

    @Test
    void testCreateHabit_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> habitResolver.createHabit("x", HabitCategory.PHYSICAL, "d"));
        verify(habitService, never()).create(any());
    }

    @Test
    void testUpdateHabit_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        Habit updated = new Habit();
        when(habitService.update(1L, "New", HabitCategory.SLEEP, "desc")).thenReturn(updated);
        Habit out = habitResolver.updateHabit(1L, "New", HabitCategory.SLEEP, "desc");
        assertNotNull(out);
    }

    @Test
    void testUpdateHabit_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> habitResolver.updateHabit(1L, null, null, null));
        verify(habitService, never()).update(anyLong(), any(), any(), any());
    }

    @Test
    void testDeleteHabit_happyPath() {
        Role role = new Role(); role.setCanDelete(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(habitService.deleteById(3L)).thenReturn(true);
        assertTrue(habitResolver.deleteHabit(3L));
        verify(habitService).deleteById(3L);
    }

    @Test
    void testDeleteHabit_noDeletePermission_throws() {
        Role role = new Role(); role.setCanDelete(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> habitResolver.deleteHabit(3L));
        verify(habitService, never()).deleteById(anyLong());
    }
}
