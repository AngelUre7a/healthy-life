package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class GuideDTOTest {
    @Test
    void testGettersAndSetters() {
        GuideDTO dto = new GuideDTO();
        Long id = 1L;
        String title = "Guía";
        String content = "Contenido";
        String category = "Salud";
        Set<Long> recommendedHabitIds = Set.of(2L, 3L);

        dto.setId(id);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setCategory(category);
        dto.setRecommendedHabitIds(recommendedHabitIds);

        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertEquals(content, dto.getContent());
        assertEquals(category, dto.getCategory());
        assertEquals(recommendedHabitIds, dto.getRecommendedHabitIds());
    }
}
