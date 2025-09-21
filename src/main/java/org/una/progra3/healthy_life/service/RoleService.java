package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.RoleRepository;
import org.una.progra3.healthy_life.repository.UserRepository;
import org.una.progra3.healthy_life.dtos.CreateRoleInput;
import org.una.progra3.healthy_life.dtos.UpdateRoleInput;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> getAllRoles() { return roleRepository.findAll(); }

    public Role getRoleById(Long id) { return roleRepository.findById(id).orElse(null); }

    @Transactional
    public Role createRole(CreateRoleInput input) {
        if (input.getName() == null) throw new IllegalArgumentException("RoleType is required");
    if (roleRepository.existsByName(input.getName())) throw new RuntimeException("Role name already exists");
        Role role = new Role();
        role.setName(input.getName());
        role.setDescription(input.getDescription());
        role.setCanRead(input.isCanRead());
        role.setCanWrite(input.isCanWrite());
        role.setCanDelete(input.isCanDelete());
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, UpdateRoleInput input) {
        Role role = getRoleById(id);
        if (role == null) throw new RuntimeException("Role not found");
        if (input.getName() != null) role.setName(input.getName());
        if (input.getDescription() != null) role.setDescription(input.getDescription());
        if (input.getCanRead() != null) role.setCanRead(input.getCanRead());
        if (input.getCanWrite() != null) role.setCanWrite(input.getCanWrite());
        if (input.getCanDelete() != null) role.setCanDelete(input.getCanDelete());
        return roleRepository.save(role);
    }

    @Transactional
    public boolean deleteRole(Long id) {
        if (roleRepository.existsById(id)) { roleRepository.deleteById(id); return true; }
        return false;
    }

    @Transactional
    public User assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = getRoleById(roleId);
        if (role == null) throw new RuntimeException("Role not found");
        user.setRole(role);
        return userRepository.save(user);
    }
}
