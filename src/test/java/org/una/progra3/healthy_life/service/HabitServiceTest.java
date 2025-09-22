package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.una.progra3.healthy_life.entity.Habit;

import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.HabitRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {
    @Test
    void testFindAllPaginated() {
        var pageInput = new org.una.progra3.healthy_life.dtos.PageInputDTO();
        pageInput.setPage(0);
        pageInput.setSize(2);
        pageInput.setSortBy("name");
        pageInput.setSortDirection("ASC");
        org.springframework.data.domain.Page<Habit> page = org.springframework.data.domain.Page.empty();
        Mockito.when(habitRepository.findAll(Mockito.any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        var result = habitService.findAllPaginated(pageInput);
        assertNotNull(result);
    }

    @Test
    void testFindByCategoryPaginated() {
        var pageInput = new org.una.progra3.healthy_life.dtos.PageInputDTO();
        pageInput.setPage(0);
        pageInput.setSize(2);
        pageInput.setSortBy("name");
        pageInput.setSortDirection("DESC");
        org.springframework.data.domain.Page<Habit> page = org.springframework.data.domain.Page.empty();
        Mockito.when(habitRepository.findByCategory(Mockito.eq(HabitCategory.PHYSICAL), Mockito.any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        var result = habitService.findByCategoryPaginated(HabitCategory.PHYSICAL, pageInput);
        assertNotNull(result);
    }

    @Test
    void testCreatePageInfo() {
        Habit habit = new Habit();
        habit.setName("Test");
        org.springframework.data.domain.Page<Habit> page = new org.springframework.data.domain.PageImpl<>(List.of(habit));
        var info = habitService.createPageInfo(page);
    assertTrue(info.isHasNextPage() == page.hasNext());
    assertTrue(info.isHasPreviousPage() == page.hasPrevious());
    assertEquals(page.getTotalElements(), info.getTotalElements());
    assertEquals(page.getTotalPages(), info.getTotalPages());
    assertEquals(page.getNumber(), info.getCurrentPage());
    assertEquals(page.getSize(), info.getPageSize());
    }
    @Mock
    HabitRepository habitRepository;

    @InjectMocks
    HabitService habitService;

    @Test
    void testFindAll() {
        Habit habit = new Habit();
        Mockito.when(habitRepository.findAll()).thenReturn(List.of(habit));
        List<Habit> result = habitService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        Habit habit = new Habit();
        habit.setId(1L);
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Habit result = habitService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByName() {
        Habit habit = new Habit();
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.findByName("Ejercicio")).thenReturn(Optional.of(habit));
        Habit result = habitService.findByName("Ejercicio");
        assertNotNull(result);
        assertEquals("Ejercicio", result.getName());
    }

    @Test
    void testFindByCategory() {
        Habit habit = new Habit();
        habit.setCategory(HabitCategory.PHYSICAL);
        Mockito.when(habitRepository.findByCategory(HabitCategory.PHYSICAL)).thenReturn(List.of(habit));
        List<Habit> result = habitService.findByCategory(HabitCategory.PHYSICAL);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateValid() {
        Habit habit = new Habit();
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.existsByName("Ejercicio")).thenReturn(false);
        Mockito.when(habitRepository.save(habit)).thenReturn(habit);
        Habit result = habitService.create(habit);
        assertEquals("Ejercicio", result.getName());
    }

    @Test
    void testCreateInvalidName() {
        Habit habit = new Habit();
        habit.setName("");
        assertThrows(IllegalArgumentException.class, () -> habitService.create(habit));
    }

    @Test
    void testCreateDuplicateName() {
        Habit habit = new Habit();
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.existsByName("Ejercicio")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> habitService.create(habit));
    }

    @Test
    void testUpdateNotFound() {
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> habitService.update(1L, "Nuevo", HabitCategory.PHYSICAL, "desc"));
    }

    @Test
    void testUpdateValid() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Mockito.when(habitRepository.existsByName("Nuevo")).thenReturn(false);
        Mockito.when(habitRepository.save(Mockito.any())).thenReturn(habit);
        Habit result = habitService.update(1L, "Nuevo", HabitCategory.PHYSICAL, "desc");
        assertNotNull(result);
    }

    @Test
    void testUpdate_NameUnchanged_DoesNotCheckExists() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Mockito.when(habitRepository.save(Mockito.any())).thenReturn(habit);

        Habit result = habitService.update(1L, "Ejercicio", null, null);
        assertNotNull(result);
        // verify that existsByName was not called when name is the same
        Mockito.verify(habitRepository, Mockito.never()).existsByName(Mockito.anyString());
    }

    @Test
    void testUpdate_CategoryOnly() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Mockito.when(habitRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Habit result = habitService.update(1L, null, HabitCategory.MENTAL, null);
        assertEquals(HabitCategory.MENTAL, result.getCategory());
    }

    @Test
    void testUpdate_DescriptionOnly() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Mockito.when(habitRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Habit result = habitService.update(1L, null, null, "nueva desc");
        assertEquals("nueva desc", result.getDescription());
    }

    @Test
    void testUpdate_AllNull_NoChanges() {
        Habit habit = new Habit();
        habit.setId(1L);
        habit.setName("Ejercicio");
        habit.setCategory(HabitCategory.PHYSICAL);
        habit.setDescription("desc");
        Mockito.when(habitRepository.findById(1L)).thenReturn(Optional.of(habit));
        Mockito.when(habitRepository.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Habit result = habitService.update(1L, null, null, null);
        assertEquals("Ejercicio", result.getName());
        assertEquals(HabitCategory.PHYSICAL, result.getCategory());
        assertEquals("desc", result.getDescription());
    }

    @Test
    void testDeleteByIdExists() {
        Mockito.when(habitRepository.existsById(1L)).thenReturn(true);
        assertTrue(habitService.deleteById(1L));
    }

    @Test
    void testDeleteByIdNotExists() {
        Mockito.when(habitRepository.existsById(2L)).thenReturn(false);
        assertFalse(habitService.deleteById(2L));
    }
}
