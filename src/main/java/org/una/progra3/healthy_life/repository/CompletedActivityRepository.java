package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.CompletedActivity;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.Habit;

import java.util.List;

@Repository
public interface CompletedActivityRepository extends JpaRepository<CompletedActivity, Long> {
    List<CompletedActivity> findByProgressLog(ProgressLog progressLog);
    List<CompletedActivity> findByHabit(Habit habit);
}
