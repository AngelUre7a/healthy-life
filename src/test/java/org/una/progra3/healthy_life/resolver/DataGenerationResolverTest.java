package org.una.progra3.healthy_life.resolver;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.dtos.DatasetGenerationResponseDTO;
import org.una.progra3.healthy_life.dtos.DatasetStatisticsDTO;
import org.una.progra3.healthy_life.service.DataGenerationService;
import org.una.progra3.healthy_life.repository.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataGenerationResolverTest {
    @Mock DataGenerationService dataGenerationService;
    @Mock UserRepository userRepository;
    @Mock HabitRepository habitRepository;
    @Mock RoutineRepository routineRepository;
    @Mock GuideRepository guideRepository;
    @Mock ReminderRepository reminderRepository;
    @Mock CompletedActivityRepository completedActivityRepository;
    @Mock ProgressLogRepository progressLogRepository;
    @Mock RoleRepository roleRepository;
    @InjectMocks DataGenerationResolver resolver;

    @Test
    void testGenerateMassiveDataset_success() {
        doNothing().when(dataGenerationService).generateMassiveDataset();
        DatasetGenerationResponseDTO response = resolver.generateMassiveDataset();
    assertTrue(response.getSuccess());
        assertTrue(response.getMessage().contains("exitosamente"));
    }

    @Test
    void testGenerateMassiveDataset_failure() {
        doThrow(new RuntimeException("fail")).when(dataGenerationService).generateMassiveDataset();
        DatasetGenerationResponseDTO response = resolver.generateMassiveDataset();
    assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("fail"));
    }

    @Test
    void testCleanGeneratedData_success() {
        doNothing().when(dataGenerationService).cleanGeneratedData();
        DatasetGenerationResponseDTO response = resolver.cleanGeneratedData();
    assertTrue(response.getSuccess());
        assertTrue(response.getMessage().contains("exitosamente"));
    }

    @Test
    void testCleanGeneratedData_failure() {
        doThrow(new RuntimeException("error")).when(dataGenerationService).cleanGeneratedData();
        DatasetGenerationResponseDTO response = resolver.cleanGeneratedData();
    assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("error"));
    }

    @Test
    void testGetDatasetStatistics() {
        when(userRepository.count()).thenReturn(1L);
        when(habitRepository.count()).thenReturn(2L);
        when(routineRepository.count()).thenReturn(3L);
        when(guideRepository.count()).thenReturn(4L);
        when(reminderRepository.count()).thenReturn(5L);
        when(completedActivityRepository.count()).thenReturn(6L);
        when(progressLogRepository.count()).thenReturn(7L);
        when(roleRepository.count()).thenReturn(8L);
        DatasetStatisticsDTO stats = resolver.getDatasetStatistics();
    assertEquals(1, stats.getUsers());
    assertEquals(2, stats.getHabits());
    assertEquals(3, stats.getRoutines());
    assertEquals(4, stats.getGuides());
    assertEquals(5, stats.getReminders());
    assertEquals(6, stats.getCompletedActivities());
    assertEquals(7, stats.getProgressLogs());
    assertEquals(8, stats.getRoles());
    }
}
