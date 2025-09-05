package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.CompletedActivity;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.repository.CompletedActivityRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompletedActivityService {

    @Autowired
    private CompletedActivityRepository completedActivityRepository;

    public List<CompletedActivity> findAll() { return completedActivityRepository.findAll(); }
    public CompletedActivity findById(Long id) { return completedActivityRepository.findById(id).orElse(null); }
    public List<CompletedActivity> findByProgressLog(ProgressLog log) { return completedActivityRepository.findByProgressLog(log); }
    public List<CompletedActivity> findByHabit(Habit habit) { return completedActivityRepository.findByHabit(habit); }

    @Transactional
    public CompletedActivity create(ProgressLog log, Habit habit, LocalDateTime completedAt, String notes) {
        if (log == null) throw new IllegalArgumentException("ProgressLog is required");
        if (habit == null) throw new IllegalArgumentException("Habit is required");
        CompletedActivity newCompletedActivity = new CompletedActivity();
        newCompletedActivity.setProgressLog(log);
        newCompletedActivity.setHabit(habit);
        newCompletedActivity.setCompletedAt(completedAt != null ? completedAt : LocalDateTime.now());
        newCompletedActivity.setNotes(notes);
        return completedActivityRepository.save(newCompletedActivity);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (completedActivityRepository.existsById(id)) { completedActivityRepository.deleteById(id); return true; }
        return false;
    }
}
