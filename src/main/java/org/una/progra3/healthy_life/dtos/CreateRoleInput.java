package org.una.progra3.healthy_life.dtos;

import org.una.progra3.healthy_life.entity.enums.RoleType;

public class CreateRoleInput {
    private RoleType name;
    private String description;
    private boolean canRead;
    private boolean canWrite;
    private boolean canDelete;

    public CreateRoleInput() {}

    public RoleType getName() { return name; }
    public void setName(RoleType name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCanRead() { return canRead; }
    public void setCanRead(boolean canRead) { this.canRead = canRead; }

    public boolean isCanWrite() { return canWrite; }
    public void setCanWrite(boolean canWrite) { this.canWrite = canWrite; }

    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
}
