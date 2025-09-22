package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.una.progra3.healthy_life.entity.*;
import org.una.progra3.healthy_life.entity.enums.RoleType;
import org.una.progra3.healthy_life.repository.*;

import java.util.*;

import static org.mockito.Mockito.*;

class DataGenerationServiceTest {
    @Test
    void testGenerateMassiveDataset_callsAllMethods() {
        DataGenerationService spyService = spy(service);
        doNothing().when(spyService).createBasicRoles();
        doNothing().when(spyService).generateUsers(anyInt());
        doNothing().when(spyService).generateHabits(anyInt());
        doNothing().when(spyService).generateRoutines(anyInt());
        doNothing().when(spyService).generateGuides(anyInt());
        doNothing().when(spyService).generateReminders(anyInt());
        doNothing().when(spyService).generateCompletedActivities(anyInt());
        spyService.generateMassiveDataset();
        verify(spyService).createBasicRoles();
        verify(spyService).generateUsers(100000);
        verify(spyService).generateHabits(10000);
        verify(spyService).generateRoutines(150000);
        verify(spyService).generateGuides(5000);
        verify(spyService).generateReminders(100000);
        verify(spyService).generateCompletedActivities(200000);
    }
    @Mock private UserRepository userRepository;
    @Mock private HabitRepository habitRepository;
    @Mock private RoutineRepository routineRepository;
    @Mock private GuideRepository guideRepository;
    @Mock private ReminderRepository reminderRepository;
    @Mock private CompletedActivityRepository completedActivityRepository;
    @Mock private ProgressLogRepository progressLogRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private DataGenerationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
    }

    @Test
    void testCreateBasicRoles_createsRolesIfNotExist() {
        when(roleRepository.existsByName(RoleType.USER)).thenReturn(false);
        when(roleRepository.existsByName(RoleType.ADMIN)).thenReturn(false);
        service.createBasicRoles();
        verify(roleRepository, times(2)).save(any(Role.class));
    }

    @Test
    void testCreateBasicRoles_doesNotCreateIfExists() {
        when(roleRepository.existsByName(RoleType.USER)).thenReturn(true);
        when(roleRepository.existsByName(RoleType.ADMIN)).thenReturn(true);
        service.createBasicRoles();
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testGenerateUsers_savesUsersInBatches() {
        when(roleRepository.findAll()).thenReturn(Collections.singletonList(new Role()));
        service.generateUsers(10);
        verify(userRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testGenerateHabits_savesHabitsInBatches() {
        service.generateHabits(10);
        verify(habitRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testGenerateRoutines_noUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        service.generateRoutines(10);
        verify(routineRepository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateRoutines_withUsers() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        service.generateRoutines(10);
        verify(routineRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testGenerateGuides_savesGuidesInBatches() {
        service.generateGuides(10);
        verify(guideRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testGenerateReminders_noUsersOrHabits() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(habitRepository.findAll()).thenReturn(Collections.emptyList());
        service.generateReminders(10);
        verify(reminderRepository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateReminders_withUsersAndHabits() {
        User user = new User();
        Habit habit = new Habit();
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(habitRepository.findAll()).thenReturn(Collections.singletonList(habit));
        service.generateReminders(10);
        verify(reminderRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testGenerateCompletedActivities_noHabits() {
        when(habitRepository.findAll()).thenReturn(Collections.emptyList());
        service.generateCompletedActivities(10);
        verify(completedActivityRepository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateCompletedActivities_withHabits() {
        Habit habit = new Habit();
        when(habitRepository.findAll()).thenReturn(Collections.singletonList(habit));
        service.generateCompletedActivities(10);
        verify(completedActivityRepository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void testCleanGeneratedData_deletesAll() {
        service.cleanGeneratedData();
        verify(completedActivityRepository).deleteAll();
        verify(reminderRepository).deleteAll();
        verify(guideRepository).deleteAll();
        verify(routineRepository).deleteAll();
        verify(habitRepository).deleteAll();
        verify(userRepository).deleteByEmailContaining("@healthylife.com");
    }

    @Test
    void testPrintDatasetStatistics_printsCounts() {
        when(userRepository.count()).thenReturn(1L);
        when(habitRepository.count()).thenReturn(2L);
        when(routineRepository.count()).thenReturn(3L);
        when(guideRepository.count()).thenReturn(4L);
        when(reminderRepository.count()).thenReturn(5L);
        when(completedActivityRepository.count()).thenReturn(6L);
        when(progressLogRepository.count()).thenReturn(7L);
        when(roleRepository.count()).thenReturn(8L);
        service.printDatasetStatistics();
        // No assertion, just coverage for print
    }

    // ...existing code...
}
