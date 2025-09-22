package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.una.progra3.healthy_life.entity.Guide;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.service.GuideService;
import org.una.progra3.healthy_life.service.HabitService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuideResolverTest {
    @Test
    void testUpdateGuide_happyPath() {
    Role role = new Role(); role.setCanWrite(true); role.setCanDelete(true);
    User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        Guide existing = new Guide(); existing.setId(1L);
        when(guideService.findById(1L)).thenReturn(existing);
        when(guideService.create(any())).thenReturn(existing);
        Guide result = guideResolver.updateGuide(1L, "title", "content", HabitCategory.PHYSICAL, List.of("1"));
        assertNotNull(result);
        verify(guideService).create(any());
        when(guideService.deleteById(5L)).thenReturn(true);
        assertTrue(guideResolver.deleteGuide(5L));
        verify(guideService).deleteById(5L);
    }

    @Test
    void testDeleteGuide_noDeletePermission_throws() {
        Role role = new Role(); role.setCanDelete(false);
        User user = new User(); user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        assertThrows(RuntimeException.class, () -> guideResolver.deleteGuide(5L));
        verify(guideService, never()).deleteById(anyLong());
    }

    @Test
    void testResolveHabits_validIds() throws Exception {
        GuideResolver resolver = new GuideResolver();
        HabitService habitServiceMock = mock(HabitService.class);
        Habit h1 = new Habit(); h1.setId(1L);
        Habit h2 = new Habit(); h2.setId(2L);
        when(habitServiceMock.findById(1L)).thenReturn(h1);
        when(habitServiceMock.findById(2L)).thenReturn(h2);
        // Inyectar el mock por reflexión
        java.lang.reflect.Field field = GuideResolver.class.getDeclaredField("habitService");
        field.setAccessible(true);
        field.set(resolver, habitServiceMock);
        java.lang.reflect.Method method = resolver.getClass().getDeclaredMethod("resolveHabits", List.class);
        method.setAccessible(true);
        Object resultObj = method.invoke(resolver, List.of("1", "2"));
        @SuppressWarnings("unchecked")
        Set<Habit> resultSet = (Set<Habit>) resultObj;
        assertEquals(2, resultSet.size());
        assertTrue(resultSet.contains(h1));
        assertTrue(resultSet.contains(h2));
    }
    @Mock GuideService guideService;
    @Mock HabitService habitService;
    @Mock AuthenticationService authenticationService;
    @InjectMocks GuideResolver guideResolver;

    // queries
    @Test
    void testGuidesByCategory_specificCategory_readsFromService() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        Guide guide = new Guide();
        when(guideService.findByCategory(HabitCategory.PHYSICAL)).thenReturn(List.of(guide));

        List<Guide> result = guideResolver.guidesByCategory(HabitCategory.PHYSICAL);

        assertEquals(1, result.size());
        verify(guideService, times(1)).findByCategory(HabitCategory.PHYSICAL);
        verify(guideService, never()).findAll();
    }

    @Test
    void testGuidesByCategory_nullCategory_returnsAll() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        when(guideService.findAll()).thenReturn(List.of(new Guide(), new Guide()));

        List<Guide> result = guideResolver.guidesByCategory(null);

        assertEquals(2, result.size());
        verify(guideService, times(1)).findAll();
        verify(guideService, never()).findByCategory(any());
    }

    @Test
    void testGuidesByCategory_noReadPermission_throws() {
        Role role = new Role();
        role.setCanRead(false);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> guideResolver.guidesByCategory(HabitCategory.MENTAL));
        verifyNoInteractions(guideService);
    }

    @Test
    void testGuideById_happyPath() {
        Role role = new Role();
        role.setCanRead(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        when(guideService.findById(1L)).thenReturn(new Guide());
        Guide result = guideResolver.guideById(1L);
        assertNotNull(result);
        verify(guideService).findById(1L);
    }

    @Test
    void testGuideById_noReadPermission_throws() {
        Role role = new Role();
        role.setCanRead(false);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> guideResolver.guideById(99L));
        verify(guideService, never()).findById(anyLong());
    }

    // create
    @Test
    void testCreateGuide_withParsedHabits_success() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Prepare habits returned by service
    Habit h1 = new Habit(); h1.setId(1L);
    Habit h2 = new Habit(); h2.setId(2L);
    Habit h4 = new Habit(); h4.setId(4L);
    Habit h5 = new Habit(); h5.setId(5L);
        when(habitService.findById(1L)).thenReturn(h1);
        when(habitService.findById(2L)).thenReturn(h2);
        when(habitService.findById(3L)).thenReturn(null); // ignored
        when(habitService.findById(4L)).thenReturn(h4);
        when(habitService.findById(5L)).thenReturn(h5);

        // Capture guide saved
        ArgumentCaptor<Guide> captor = ArgumentCaptor.forClass(Guide.class);
        when(guideService.create(any(Guide.class))).thenAnswer(inv -> inv.getArgument(0));

    List<String> raw = Arrays.asList("1", " [2, 3] ", "bad", "", "4, 5", " [ 2 ] ", null);
    Guide saved = guideResolver.createGuide("T", "C", HabitCategory.DIET, raw);

        assertNotNull(saved);
        verify(guideService).create(captor.capture());
        Guide toSave = captor.getValue();
        assertEquals("T", toSave.getTitle());
        assertEquals("C", toSave.getContent());
    assertEquals(HabitCategory.DIET, toSave.getCategory());
        assertNotNull(toSave.getRecommendedFor());
        // expecting unique habits: 1,2,4,5 -> 4 elements
        assertEquals(4, toSave.getRecommendedFor().size());
        assertTrue(toSave.getRecommendedFor().contains(h1));
        assertTrue(toSave.getRecommendedFor().contains(h2));
        assertTrue(toSave.getRecommendedFor().contains(h4));
        assertTrue(toSave.getRecommendedFor().contains(h5));
    }

    @Test
    void testCreateGuide_noWritePermission_throws() {
        Role role = new Role();
        role.setCanWrite(false);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> guideResolver.createGuide("T", "C", HabitCategory.SLEEP, List.of("1")));
        verifyNoInteractions(guideService);
    }

    @Test
    void testCreateGuide_nullRecommendedHabits_createsEmptySet() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        ArgumentCaptor<Guide> captor = ArgumentCaptor.forClass(Guide.class);
        when(guideService.create(any(Guide.class))).thenAnswer(inv -> inv.getArgument(0));

        Guide saved = guideResolver.createGuide("T2", "C2", HabitCategory.MENTAL, null);
        assertNotNull(saved);
        verify(guideService).create(captor.capture());
        Guide toSave = captor.getValue();
        assertNotNull(toSave.getRecommendedFor());
        assertEquals(0, toSave.getRecommendedFor().size());
    }

    // update
    @Test
    void testUpdateGuide_notFound_throws() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        when(guideService.findById(123L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> guideResolver.updateGuide(123L, "T", null, null, null));
        verify(guideService, never()).create(any());
    }

    @Test
    void testUpdateGuide_partialUpdate_preservesHabitsWhenNull() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        Guide existing = new Guide();
        existing.setTitle("Old");
        existing.setContent("OldC");
        existing.setCategory(HabitCategory.PHYSICAL);
        Habit h1 = new Habit();
        Set<Habit> rec = new HashSet<>();
        rec.add(h1);
        existing.setRecommendedFor(rec);

        when(guideService.findById(7L)).thenReturn(existing);
        ArgumentCaptor<Guide> captor = ArgumentCaptor.forClass(Guide.class);
        when(guideService.create(any(Guide.class))).thenAnswer(inv -> inv.getArgument(0));

        Guide result = guideResolver.updateGuide(7L, "New", null, null, null);
        assertNotNull(result);
        verify(guideService).create(captor.capture());
        Guide toSave = captor.getValue();
        assertEquals("New", toSave.getTitle());
        assertEquals("OldC", toSave.getContent());
        assertEquals(HabitCategory.PHYSICAL, toSave.getCategory());
        assertNotNull(toSave.getRecommendedFor());
        assertEquals(1, toSave.getRecommendedFor().size());
        assertTrue(toSave.getRecommendedFor().contains(h1));
    }

    @Test
    void testUpdateGuide_replaceHabitsWhenProvided() {
        Role role = new Role();
        role.setCanWrite(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        Guide existing = new Guide();
    Habit old = new Habit(); old.setId(1L);
        Set<Habit> rec = new HashSet<>();
        rec.add(old);
        existing.setRecommendedFor(rec);

        when(guideService.findById(9L)).thenReturn(existing);
    Habit h7 = new Habit(); h7.setId(7L);
    Habit h8 = new Habit(); h8.setId(8L);
        when(habitService.findById(7L)).thenReturn(h7);
        when(habitService.findById(8L)).thenReturn(h8);

        ArgumentCaptor<Guide> captor = ArgumentCaptor.forClass(Guide.class);
        when(guideService.create(any(Guide.class))).thenAnswer(inv -> inv.getArgument(0));

        Guide result = guideResolver.updateGuide(9L, null, null, null, List.of("7", "8"));
        assertNotNull(result);
        verify(guideService).create(captor.capture());
        Guide toSave = captor.getValue();
        assertEquals(2, toSave.getRecommendedFor().size());
        assertTrue(toSave.getRecommendedFor().contains(h7));
        assertTrue(toSave.getRecommendedFor().contains(h8));
        assertFalse(toSave.getRecommendedFor().contains(old));
    }

    // delete
    @Test
    void testDeleteGuide_success() {
        Role role = new Role();
        role.setCanDelete(true);
        User user = new User();
        user.setRole(role);
        when(authenticationService.getCurrentUser()).thenReturn(user);

        when(guideService.deleteById(42L)).thenReturn(true);
        boolean ok = guideResolver.deleteGuide(42L);
        assertTrue(ok);
        verify(guideService).deleteById(42L);
    }

}
