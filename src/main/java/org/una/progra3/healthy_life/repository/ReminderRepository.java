package org.una.progra3.healthy_life.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.una.progra3.healthy_life.entity.Reminder;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser(User user);
    List<Reminder> findByHabit(Habit habit);
    List<Reminder> findByUserAndFrequency(User user, ReminderFrequency frequency);
}
