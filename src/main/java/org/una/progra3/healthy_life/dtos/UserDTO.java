
package org.una.progra3.healthy_life.dtos;

import java.util.Set;
import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Long roleId;
    private Set<Long> favoriteHabitIds;
    private List<Long> routineIds;
    private List<Long> progressLogIds;
    private List<Long> reminderIds;
    private List<Long> authTokenIds;

    public UserDTO() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Set<Long> getFavoriteHabitIds() {
        return favoriteHabitIds;
    }
    public void setFavoriteHabitIds(Set<Long> favoriteHabitIds) {
        this.favoriteHabitIds = favoriteHabitIds;
    }

    public List<Long> getRoutineIds() {
        return routineIds;
    }
    public void setRoutineIds(List<Long> routineIds) {
        this.routineIds = routineIds;
    }

    public List<Long> getProgressLogIds() {
        return progressLogIds;
    }
    public void setProgressLogIds(List<Long> progressLogIds) {
        this.progressLogIds = progressLogIds;
    }

    public List<Long> getReminderIds() {
        return reminderIds;
    }
    public void setReminderIds(List<Long> reminderIds) {
        this.reminderIds = reminderIds;
    }

    public List<Long> getAuthTokenIds() {
        return authTokenIds;
    }
    public void setAuthTokenIds(List<Long> authTokenIds) {
        this.authTokenIds = authTokenIds;
    }
}
