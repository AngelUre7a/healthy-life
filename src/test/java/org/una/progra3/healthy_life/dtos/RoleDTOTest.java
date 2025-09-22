package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.una.progra3.healthy_life.entity.enums.RoleType;

public class RoleDTOTest {
    @Test
    void testGettersAndSetters() {
        RoleDTO dto = new RoleDTO();
        Long id = 1L;
        RoleType name = RoleType.ADMIN;
        String description = "Administrador";
        boolean canRead = true;
        boolean canWrite = false;
        boolean canDelete = true;

        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setCanRead(canRead);
        dto.setCanWrite(canWrite);
        dto.setCanDelete(canDelete);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
        assertEquals(canRead, dto.isCanRead());
        assertEquals(canWrite, dto.isCanWrite());
        assertEquals(canDelete, dto.isCanDelete());
    }
}
