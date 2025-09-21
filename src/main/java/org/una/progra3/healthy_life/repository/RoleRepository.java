package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.Role;

import java.util.Optional;
import org.una.progra3.healthy_life.entity.enums.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
    boolean existsByName(RoleType name);
}
