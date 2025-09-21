package org.una.progra3.healthy_life.security;

import org.una.progra3.healthy_life.entity.User;

public class PermissionValidator {
    public static void checkRead(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanRead()) {
            throw new RuntimeException("You do not have READ permission");
        }
    }

    public static void checkWrite(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanWrite()) {
            throw new RuntimeException("You do not have WRITE permission");
        }
    }

    public static void checkDelete(User user) {
        if (user == null || user.getRole() == null || !user.getRole().isCanDelete()) {
            throw new RuntimeException("You do not have DELETE permission");
        }
    }
}
