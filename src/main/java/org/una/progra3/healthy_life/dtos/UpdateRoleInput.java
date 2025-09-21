package org.una.progra3.healthy_life.dtos;

import org.una.progra3.healthy_life.entity.enums.RoleType;

public class UpdateRoleInput {
    private RoleType name;
    private String description;
    private Boolean canRead;
    private Boolean canWrite;
    private Boolean canDelete;

    public UpdateRoleInput() {}

    public RoleType getName() { return name; }
    public void setName(RoleType name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getCanRead() { return canRead; }
    public void setCanRead(Boolean canRead) { this.canRead = canRead; }

    public Boolean getCanWrite() { return canWrite; }
    public void setCanWrite(Boolean canWrite) { this.canWrite = canWrite; }

    public Boolean getCanDelete() { return canDelete; }
    public void setCanDelete(Boolean canDelete) { this.canDelete = canDelete; }
}
