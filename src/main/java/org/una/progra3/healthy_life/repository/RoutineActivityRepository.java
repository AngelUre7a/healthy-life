package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.RoutineActivity;
import org.una.progra3.healthy_life.entity.Routine;

import java.util.List;

@Repository
public interface RoutineActivityRepository extends JpaRepository<RoutineActivity, Long> {
    List<RoutineActivity> findByRoutine(Routine routine);
}
