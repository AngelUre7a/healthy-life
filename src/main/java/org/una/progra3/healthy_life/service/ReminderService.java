package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Reminder;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.ReminderFrequency;
import org.una.progra3.healthy_life.repository.ReminderRepository;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    public List<Reminder> findAll() { return reminderRepository.findAll(); }
    public Reminder findById(Long id) { return reminderRepository.findById(id).orElse(null); }
    public List<Reminder> findByUser(User user) { return reminderRepository.findByUser(user); }
    public List<Reminder> findByHabit(Habit habit) { return reminderRepository.findByHabit(habit); }
    public List<Reminder> findByUserAndFrequency(User user, ReminderFrequency frequency) { return reminderRepository.findByUserAndFrequency(user, frequency); }

    @Transactional
    public Reminder create(User user, Habit habit, LocalTime time, ReminderFrequency frequency) {
        if (user == null) throw new IllegalArgumentException("User is required");
        if (habit == null) throw new IllegalArgumentException("Habit is required");
        if (time == null) throw new IllegalArgumentException("Time is required");
        if (frequency == null) throw new IllegalArgumentException("Frequency is required");
        Reminder newReminder = new Reminder();
        newReminder.setUser(user);
        newReminder.setHabit(habit);
        newReminder.setTime(time);
        newReminder.setFrequency(frequency);
        return reminderRepository.save(newReminder);
    }

    @Transactional
    public Reminder update(Long id, LocalTime time, ReminderFrequency frequency) {
        Reminder existing = findById(id);
        if (existing == null) throw new RuntimeException("Reminder not found");
        if (time != null) existing.setTime(time);
        if (frequency != null) existing.setFrequency(frequency);
        return reminderRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (reminderRepository.existsById(id)) { reminderRepository.deleteById(id); return true; }
        return false;
    }
}
