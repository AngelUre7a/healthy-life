package org.una.progra3.healthy_life.dtos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.una.progra3.healthy_life.entity.enums.RoleType;

public class UpdateRoleInputTest {
    @Test
    void testGettersAndSetters() {
        UpdateRoleInput input = new UpdateRoleInput();
        RoleType name = RoleType.ADMIN;
        String description = "desc";
        Boolean canRead = true;
        Boolean canWrite = false;
        Boolean canDelete = true;

        input.setName(name);
        input.setDescription(description);
        input.setCanRead(canRead);
        input.setCanWrite(canWrite);
        input.setCanDelete(canDelete);

        assertEquals(name, input.getName());
        assertEquals(description, input.getDescription());
        assertEquals(canRead, input.getCanRead());
        assertEquals(canWrite, input.getCanWrite());
        assertEquals(canDelete, input.getCanDelete());
    }
}
