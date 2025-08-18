package org.una.progra3.healthy_life.dtos;

public class RoleDTO {
    private Long id;
    private String name;
    private String permissions;

    public RoleDTO() {}

    public RoleDTO(Long id, String name, String permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

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

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
