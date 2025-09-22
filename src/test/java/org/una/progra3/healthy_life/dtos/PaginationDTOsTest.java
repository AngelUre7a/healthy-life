package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PaginationDTOsTest {

    @Test
    void pageInfoDTO_allFields() {
        PageInfoDTO p = new PageInfoDTO(true, false, "s", "e", 10, 2, 1, 5);
        assertTrue(p.isHasNextPage());
        assertFalse(p.isHasPreviousPage());
        assertEquals("s", p.getStartCursor());
        assertEquals("e", p.getEndCursor());
        assertEquals(10, p.getTotalElements());
        assertEquals(2, p.getTotalPages());
        assertEquals(1, p.getCurrentPage());
        assertEquals(5, p.getPageSize());

        p.setHasNextPage(false);
        p.setHasPreviousPage(true);
        p.setStartCursor("s2");
        p.setEndCursor("e2");
        p.setTotalElements(20);
        p.setTotalPages(4);
        p.setCurrentPage(2);
        p.setPageSize(10);

        assertFalse(p.isHasNextPage());
        assertTrue(p.isHasPreviousPage());
        assertEquals("s2", p.getStartCursor());
        assertEquals("e2", p.getEndCursor());
        assertEquals(20, p.getTotalElements());
        assertEquals(4, p.getTotalPages());
        assertEquals(2, p.getCurrentPage());
        assertEquals(10, p.getPageSize());
    }

    @Test
    void pageInputDTO_validations() {
        PageInputDTO def = new PageInputDTO();
        assertEquals(0, def.getPage());
        assertEquals(20, def.getSize());
        assertEquals("id", def.getSortBy());
        assertEquals("ASC", def.getSortDirection());

        PageInputDTO p = new PageInputDTO(-5, 1000, null, "desc");
        assertEquals(0, p.getPage());
        assertEquals(100, p.getSize());
        assertEquals("id", p.getSortBy());
        assertEquals("DESC", p.getSortDirection());

        p.setPage(-1);
        p.setSize(0);
        p.setSortBy(null);
        p.setSortDirection("weird");
        assertEquals(0, p.getPage());
        assertEquals(1, p.getSize());
        assertEquals("id", p.getSortBy());
        assertEquals("ASC", p.getSortDirection());
    }

    @Test
    void pagedResponse_generics() {
        PageInfoDTO info = new PageInfoDTO(true, true, "a", "b", 3, 1, 0, 3);
        PagedResponseDTO<String> pr = new PagedResponseDTO<>(List.of("x","y","z"), info);
        assertEquals(3, pr.getContent().size());
        assertEquals("a", pr.getPageInfo().getStartCursor());

        HabitPagedResponseDTO hpr = new HabitPagedResponseDTO(List.of(new HabitDTO(), new HabitDTO()), info);
        assertEquals(2, hpr.getContent().size());
        assertEquals(3, hpr.getPageInfo().getTotalElements());

        RoutinePagedResponseDTO rpr = new RoutinePagedResponseDTO(List.of(new RoutineDTO()), info);
        assertEquals(1, rpr.getContent().size());

        UserPagedResponseDTO upr = new UserPagedResponseDTO(List.of(new UserDTO()), info);
        assertEquals(1, upr.getContent().size());
    }

    @Test
    void pagedResponseDTO_defaultConstructor_and_setters() {
        PagedResponseDTO<String> dto = new PagedResponseDTO<>();
        // defaults
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        // set nulls explicitly (no-op but covers setters)
        dto.setContent(null);
        dto.setPageInfo(null);
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        // set values
        PageInfoDTO info = new PageInfoDTO(false, true, "s0", "e0", 0, 0, 0, 0);
        dto.setContent(List.of("a", "b"));
        dto.setPageInfo(info);
        assertEquals(2, dto.getContent().size());
        assertEquals("s0", dto.getPageInfo().getStartCursor());
    }

    @Test
    void userPagedResponseDTO_defaultConstructor_and_setters() {
        UserPagedResponseDTO dto = new UserPagedResponseDTO();
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        dto.setContent(null);
        dto.setPageInfo(null);
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        PageInfoDTO info = new PageInfoDTO(true, false, null, null, 10, 1, 0, 10);
        dto.setContent(List.of(new UserDTO(), new UserDTO()));
        dto.setPageInfo(info);
        assertEquals(2, dto.getContent().size());
        assertEquals(10, dto.getPageInfo().getTotalElements());
    }

    @Test
    void habitPagedResponseDTO_defaultConstructor_and_setters() {
        HabitPagedResponseDTO dto = new HabitPagedResponseDTO();
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        dto.setContent(null);
        dto.setPageInfo(null);
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        PageInfoDTO info = new PageInfoDTO(false, false, "s", "e", 5, 1, 0, 5);
        dto.setContent(List.of(new HabitDTO()));
        dto.setPageInfo(info);
        assertEquals(1, dto.getContent().size());
        assertEquals("e", dto.getPageInfo().getEndCursor());
    }

    @Test
    void routinePagedResponseDTO_defaultConstructor_and_setters() {
        RoutinePagedResponseDTO dto = new RoutinePagedResponseDTO();
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        dto.setContent(null);
        dto.setPageInfo(null);
        assertNull(dto.getContent());
        assertNull(dto.getPageInfo());

        PageInfoDTO info = new PageInfoDTO(true, true, "sa", "ea", 2, 1, 0, 2);
        dto.setContent(List.of(new RoutineDTO(), new RoutineDTO()));
        dto.setPageInfo(info);
        assertEquals(2, dto.getContent().size());
        assertTrue(dto.getPageInfo().isHasNextPage());
        assertTrue(dto.getPageInfo().isHasPreviousPage());
    }

    @Test
    void pageInputDTO_twoArgConstructor_clamps() {
        PageInputDTO p1 = new PageInputDTO(-2, 1000);
        assertEquals(0, p1.getPage());
        assertEquals(100, p1.getSize());

        PageInputDTO p2 = new PageInputDTO(5, 0);
        assertEquals(5, p2.getPage());
        assertEquals(1, p2.getSize());

        // ensure setter honors valid ASC too
        p2.setSortDirection("ASC");
        assertEquals("ASC", p2.getSortDirection());
    }
}
