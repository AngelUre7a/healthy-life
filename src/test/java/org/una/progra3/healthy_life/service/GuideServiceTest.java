package org.una.progra3.healthy_life.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.una.progra3.healthy_life.entity.Guide;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.GuideRepository;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GuideServiceTest {
    @Mock
    GuideRepository guideRepository;
    @InjectMocks
    GuideService guideService;

    @Test
    void testFindAll() {
        Guide guide = new Guide();
        Mockito.when(guideRepository.findAll()).thenReturn(List.of(guide));
        List<Guide> result = guideService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        Guide guide = new Guide();
        guide.setId(1L);
        Mockito.when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));
        Guide result = guideService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByCategory() {
        Guide guide = new Guide();
        guide.setCategory(HabitCategory.PHYSICAL);
        Mockito.when(guideRepository.findByCategory(HabitCategory.PHYSICAL)).thenReturn(List.of(guide));
        List<Guide> result = guideService.findByCategory(HabitCategory.PHYSICAL);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateValid() {
        Guide guide = new Guide();
        guide.setTitle("Guía");
        Mockito.when(guideRepository.save(guide)).thenReturn(guide);
        Guide result = guideService.create(guide);
        assertEquals("Guía", result.getTitle());
    }

    @Test
    void testCreateInvalidTitle() {
        Guide guide = new Guide();
        guide.setTitle("");
        assertThrows(IllegalArgumentException.class, () -> guideService.create(guide));
    }

    @Test
    void testUpdateNotFound() {
        Mockito.when(guideRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> guideService.update(1L, "Nuevo", "Contenido", HabitCategory.PHYSICAL));
    }

    @Test
    void testUpdateValid() {
        Guide guide = new Guide();
        guide.setId(1L);
        guide.setTitle("Guía");
        Mockito.when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));
        Mockito.when(guideRepository.save(Mockito.any())).thenReturn(guide);
        Guide result = guideService.update(1L, "Nuevo", "Contenido", HabitCategory.PHYSICAL);
        assertNotNull(result);
    }

    @Test
    void testUpdateAllNull_NoChanges() {
        Guide guide = new Guide();
        guide.setId(1L);
        guide.setTitle("Old");
        guide.setContent("OldC");
        Mockito.when(guideRepository.findById(1L)).thenReturn(Optional.of(guide));
        Mockito.when(guideRepository.save(Mockito.any())).thenReturn(guide);
        Guide result = guideService.update(1L, null, null, null);
        assertNotNull(result);
        assertEquals("Old", result.getTitle());
        assertEquals("OldC", result.getContent());
    }

    @Test
    void testDeleteByIdExists() {
        Mockito.when(guideRepository.existsById(1L)).thenReturn(true);
        assertTrue(guideService.deleteById(1L));
    }

    @Test
    void testDeleteByIdNotExists() {
        Mockito.when(guideRepository.existsById(2L)).thenReturn(false);
        assertFalse(guideService.deleteById(2L));
    }
}
