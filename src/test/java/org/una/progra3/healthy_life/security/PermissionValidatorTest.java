package org.una.progra3.healthy_life.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.enums.RoleType;

class PermissionValidatorTest {

    private static User userWith(boolean read, boolean write, boolean delete) {
        Role role = new Role();
        role.setName(RoleType.USER);
        role.setDescription("test");
        role.setCanRead(read);
        role.setCanWrite(write);
        role.setCanDelete(delete);

        User u = new User();
        u.setId(1L);
        u.setName("Test");
        u.setEmail("t@example.com");
        u.setPassword("secret");
        u.setRole(role);
        return u;
    }

    @Test
    void constructor_isCallable() {
        new PermissionValidator();
    }

    @Test
    void checkRead_allowsWhenPermissionTrue() {
        assertDoesNotThrow(() -> PermissionValidator.checkRead(userWith(true, false, false)));
    }

    @Test
    void checkRead_throwsWhenPermissionMissing() {
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkRead(userWith(false, true, true)));
    }

    @Test
    void checkWrite_allowsWhenPermissionTrue() {
        assertDoesNotThrow(() -> PermissionValidator.checkWrite(userWith(false, true, false)));
    }

    @Test
    void checkWrite_throwsWhenPermissionMissing() {
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkWrite(userWith(true, false, true)));
    }

    @Test
    void checkDelete_allowsWhenPermissionTrue() {
        assertDoesNotThrow(() -> PermissionValidator.checkDelete(userWith(false, false, true)));
    }

    @Test
    void checkDelete_throwsWhenPermissionMissing() {
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkDelete(userWith(true, true, false)));
    }

    @Test
    void checks_throwWhenUserOrRoleNull() {
        // user null
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkRead(null));
        // role null for write
        User u = new User();
        u.setName("no-role");
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkWrite(u));

        // role null for read
        User u2 = new User();
        u2.setName("no-role-read");
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkRead(u2));

        // role null for delete
        User u3 = new User();
        u3.setName("no-role-del");
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkDelete(u3));

        // user null for write and delete
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkWrite(null));
        assertThrows(RuntimeException.class, () -> PermissionValidator.checkDelete(null));
    }
}
