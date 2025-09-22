package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.dtos.UserDTO;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.service.HabitService;
import org.una.progra3.healthy_life.service.UserService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserResolverTest {
    @Mock UserService userService;
    @Mock AuthenticationService authenticationService;
    @Mock HabitService habitService;
    @InjectMocks UserResolver userResolver;

    // toDTO mapping
    @Test
    void testToDTO_basic() {
        Role role = new Role();
        role.setId(3L);
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("a@a.com");
        user.setRole(role);
        UserDTO dto = userResolver.toDTO(user);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("a@a.com", dto.getEmail());
        assertEquals(3L, dto.getRoleId());
    }

    @Test
    void testToDTO_fullCollections() {
        User user = new User();
        user.setId(2L);
        // favorite habits
        Habit h1 = new Habit(); h1.setId(10L);
        Habit h2 = new Habit(); h2.setId(11L);
        user.setFavoriteHabits(Set.of(h1, h2));
        // routines
        Routine r1 = new Routine(); r1.setId(20L);
        user.setRoutines(List.of(r1));
        // progress logs
        ProgressLog pl = new ProgressLog(); pl.setId(30L);
        user.setProgressLogs(List.of(pl));
        // reminders
        Reminder rm = new Reminder(); rm.setId(40L);
        user.setReminders(List.of(rm));
        // auth tokens
        AuthToken at = new AuthToken(); at.setId(50L);
        user.setAuthTokens(List.of(at));

        UserDTO dto = userResolver.toDTO(user);
        assertEquals(Set.of(10L, 11L), dto.getFavoriteHabitIds());
        assertEquals(List.of(20L), dto.getRoutineIds());
        assertEquals(List.of(30L), dto.getProgressLogIds());
        assertEquals(List.of(40L), dto.getReminderIds());
        assertEquals(List.of(50L), dto.getAuthTokenIds());
    }

    @Test
    void testToDTO_nullUser_returnsNull() {
        assertNull(userResolver.toDTO(null));
    }

    // queries
    @Test
    void testAllUsers_returnsDTOList() {
        User u1 = new User(); u1.setId(1L);
        User u2 = new User(); u2.setId(2L);
        when(userService.findAll()).thenReturn(List.of(u1, u2));
        List<UserDTO> list = userResolver.allUsersSimple();
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void testUserById_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        User u = new User(); u.setId(7L);
        when(userService.findById(7L)).thenReturn(u);
        UserDTO dto = userResolver.userById(7L);
        assertNotNull(dto);
        assertEquals(7L, dto.getId());
    }

    @Test
    void testUserById_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> userResolver.userById(1L));
        verify(userService, never()).findById(anyLong());
    }

    @Test
    void testUserByEmail_happyPath() {
        Role role = new Role(); role.setCanRead(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        User u = new User(); u.setId(9L); u.setEmail("x@y.com");
        when(userService.findByEmail("x@y.com")).thenReturn(u);
        UserDTO dto = userResolver.userByEmail("x@y.com");
        assertNotNull(dto);
        assertEquals(9L, dto.getId());
    }

    @Test
    void testUserByEmail_noReadPermission_throws() {
        Role role = new Role(); role.setCanRead(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> userResolver.userByEmail("x@y.com"));
        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void testCurrentUser_returnsNull() {
        assertNull(userResolver.currentUser(null));
    }

    // mutations
    @Test
    void testCreateUser_savesAndReturnsDTO() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User saved = new User(); saved.setId(22L);
        when(userService.save(any(User.class))).thenReturn(saved);
        UserDTO dto = userResolver.createUser("Bob", "b@b.com", "pwd");
        assertNotNull(dto);
        assertEquals(22L, dto.getId());
        verify(userService).save(captor.capture());
        User toSave = captor.getValue();
        assertEquals("Bob", toSave.getName());
        assertEquals("b@b.com", toSave.getEmail());
        assertEquals("pwd", toSave.getPassword());
    }

    @Test
    void testUpdateUser_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        User updated = new User(); updated.setId(33L);
        when(userService.update(33L, "New", "n@n.com", "p2")).thenReturn(updated);
        UserDTO dto = userResolver.updateUser(33L, "New", "n@n.com", "p2");
        assertNotNull(dto);
        assertEquals(33L, dto.getId());
    }

    @Test
    void testUpdateUser_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> userResolver.updateUser(1L, null, null, null));
        verify(userService, never()).update(anyLong(), any(), any(), any());
    }

    @Test
    void testDeleteUser_happyPath() {
        Role role = new Role(); role.setCanDelete(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        when(userService.deleteById(55L)).thenReturn(true);
        assertTrue(userResolver.deleteUser(55L));
        verify(userService).deleteById(55L);
    }

    @Test
    void testDeleteUser_noDeletePermission_throws() {
        Role role = new Role(); role.setCanDelete(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> userResolver.deleteUser(1L));
        verify(userService, never()).deleteById(anyLong());
    }

    @Test
    void testToggleFavoriteHabit_happyPath() {
        Role role = new Role(); role.setCanWrite(true);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        Habit h = new Habit(); h.setId(77L);
        when(habitService.findById(77L)).thenReturn(h);
        User updated = new User(); updated.setId(88L);
        when(userService.toggleFavorite(88L, h)).thenReturn(updated);
        UserDTO dto = userResolver.toggleFavoriteHabit(88L, 77L);
        assertNotNull(dto);
        assertEquals(88L, dto.getId());
        verify(habitService).findById(77L);
        verify(userService).toggleFavorite(88L, h);
    }

    @Test
    void testToggleFavoriteHabit_noWritePermission_throws() {
        Role role = new Role(); role.setCanWrite(false);
        User current = new User(); current.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(current);
        assertThrows(RuntimeException.class, () -> userResolver.toggleFavoriteHabit(1L, 2L));
        verifyNoInteractions(habitService);
        verify(userService, never()).toggleFavorite(anyLong(), any());
    }
}
