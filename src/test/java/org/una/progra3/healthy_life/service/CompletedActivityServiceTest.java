package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.CompletedActivity;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.repository.CompletedActivityRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CompletedActivityServiceTest {
	@Mock
	CompletedActivityRepository completedActivityRepository;
	@InjectMocks
	CompletedActivityService completedActivityService;

	@Test
	void testFindAll() {
		CompletedActivity ca = new CompletedActivity();
		Mockito.when(completedActivityRepository.findAll()).thenReturn(List.of(ca));
		List<CompletedActivity> result = completedActivityService.findAll();
		assertEquals(1, result.size());
	}

	@Test
	void testFindById() {
		CompletedActivity ca = new CompletedActivity();
		ca.setId(1L);
		Mockito.when(completedActivityRepository.findById(1L)).thenReturn(Optional.of(ca));
		CompletedActivity result = completedActivityService.findById(1L);
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void testFindByProgressLog() {
		ProgressLog log = new ProgressLog();
		CompletedActivity ca = new CompletedActivity();
		Mockito.when(completedActivityRepository.findByProgressLog(log)).thenReturn(List.of(ca));
		List<CompletedActivity> result = completedActivityService.findByProgressLog(log);
		assertEquals(1, result.size());
	}

	@Test
	void testFindByHabit() {
		Habit habit = new Habit();
		CompletedActivity ca = new CompletedActivity();
		Mockito.when(completedActivityRepository.findByHabit(habit)).thenReturn(List.of(ca));
		List<CompletedActivity> result = completedActivityService.findByHabit(habit);
		assertEquals(1, result.size());
	}

	@Test
	void testCreateValid() {
		ProgressLog log = new ProgressLog();
		Habit habit = new Habit();
		LocalDateTime completedAt = LocalDateTime.now();
		String notes = "Test";
		CompletedActivity ca = new CompletedActivity();
		Mockito.when(completedActivityRepository.save(Mockito.any())).thenReturn(ca);
		CompletedActivity result = completedActivityService.create(log, habit, completedAt, notes);
		assertNotNull(result);
	}

	@Test
	void testCreateNullCompletedAt_DefaultsToNow() {
		ProgressLog log = new ProgressLog();
		Habit habit = new Habit();
		Mockito.when(completedActivityRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
		CompletedActivity result = completedActivityService.create(log, habit, null, "notes");
		assertNotNull(result.getCompletedAt());
	}

	@Test
	void testCreateNullLog() {
		Habit habit = new Habit();
		assertThrows(IllegalArgumentException.class, () -> completedActivityService.create(null, habit, LocalDateTime.now(), "notes"));
	}

	@Test
	void testCreateNullHabit() {
		ProgressLog log = new ProgressLog();
		assertThrows(IllegalArgumentException.class, () -> completedActivityService.create(log, null, LocalDateTime.now(), "notes"));
	}

	@Test
	void testDeleteByIdExists() {
		Mockito.when(completedActivityRepository.existsById(1L)).thenReturn(true);
		Mockito.doNothing().when(completedActivityRepository).deleteById(1L);
		assertTrue(completedActivityService.deleteById(1L));
	}

	@Test
	void testDeleteByIdNotExists() {
		Mockito.when(completedActivityRepository.existsById(2L)).thenReturn(false);
		assertFalse(completedActivityService.deleteById(2L));
	}
}
