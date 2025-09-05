package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Routine;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgressLogRepository extends JpaRepository<ProgressLog, Long> {
    List<ProgressLog> findByUser(User user);
    List<ProgressLog> findByRoutine(Routine routine);
    List<ProgressLog> findByUserAndDate(User user, LocalDate date);
}
