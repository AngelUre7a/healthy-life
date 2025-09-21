package org.una.progra3.healthy_life.resolver;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.dtos.CreateRoleInput;
import org.una.progra3.healthy_life.dtos.UpdateRoleInput;
import org.una.progra3.healthy_life.service.RoleService;

import org.una.progra3.healthy_life.service.AuthenticationService;
import org.una.progra3.healthy_life.security.PermissionValidator;

import java.util.List;

@Controller
public class RoleResolver {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthenticationService authenticationService;

    @QueryMapping
    public List<Role> allRoles() {
        return roleService.getAllRoles();
    }

    @QueryMapping
    public Role roleById(@Argument Long id) {
        return roleService.getRoleById(id);
    }

    @MutationMapping
    public Role createRole(@Argument CreateRoleInput input) {
        return roleService.createRole(input);
    }

    @MutationMapping
    public Role updateRole(@Argument Long id, @Argument UpdateRoleInput input) {
        return roleService.updateRole(id, input);
    }

    @MutationMapping
    public Boolean deleteRole(@Argument Long id) {
        return roleService.deleteRole(id);
    }

    @MutationMapping
    public User assignRoleToUser(@Argument Long userId, @Argument Long roleId) {
        return roleService.assignRoleToUser(userId, roleId);
    }
}