package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Role;
import org.una.progra3.healthy_life.repository.RoleRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> findAll() { return roleRepository.findAll(); }
    public Role findById(Long id) { return roleRepository.findById(id).orElse(null); }
    public Role findByName(String name) { return roleRepository.findByName(name).orElse(null); }

    @Transactional
    public Role create(Role role) {
        if (role.getName() == null || role.getName().isBlank()) throw new IllegalArgumentException("Role name is required");
        if (roleRepository.existsByName(role.getName())) throw new RuntimeException("Role name already exists");
        return roleRepository.save(role);
    }

    @Transactional
    public Role update(Long id, String name, String permissions) {
        Role existing = findById(id);
        if (existing == null) throw new RuntimeException("Role not found");
        if (name != null && !name.equals(existing.getName())) {
            if (roleRepository.existsByName(name)) throw new RuntimeException("Role name already exists");
            existing.setName(name);
        }
        if (permissions != null) existing.setPermissions(permissions);
        return roleRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (roleRepository.existsById(id)) { roleRepository.deleteById(id); return true; }
        return false;
    }
}
