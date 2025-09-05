package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.User;

import java.util.List;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUser(User user);
}
