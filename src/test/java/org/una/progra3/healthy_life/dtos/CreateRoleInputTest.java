package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.una.progra3.healthy_life.entity.enums.RoleType;

public class CreateRoleInputTest {
    @Test
    void testGettersAndSetters() {
        CreateRoleInput input = new CreateRoleInput();
        RoleType name = RoleType.USER;
        String description = "desc";
        boolean canRead = true;
        boolean canWrite = false;
        boolean canDelete = true;

        input.setName(name);
        input.setDescription(description);
        input.setCanRead(canRead);
        input.setCanWrite(canWrite);
        input.setCanDelete(canDelete);

        assertEquals(name, input.getName());
        assertEquals(description, input.getDescription());
        assertEquals(canRead, input.isCanRead());
        assertEquals(canWrite, input.isCanWrite());
        assertEquals(canDelete, input.isCanDelete());
    }
}
