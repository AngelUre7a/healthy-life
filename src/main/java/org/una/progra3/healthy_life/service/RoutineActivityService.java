package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.RoutineActivity;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.repository.RoutineActivityRepository;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoutineActivityService {

    @Autowired
    private RoutineActivityRepository routineActivityRepository;

    public List<RoutineActivity> findAll() { return routineActivityRepository.findAll(); }
    public RoutineActivity findById(Long id) { return routineActivityRepository.findById(id).orElse(null); }
    public List<RoutineActivity> findByRoutine(Routine routine) { return routineActivityRepository.findByRoutine(routine); }

    @Transactional
    public RoutineActivity create(Routine routine, Habit habit, Integer duration, LocalTime targetTime, String notes) {
    if (routine == null) throw new IllegalArgumentException("Routine is required");
    if (habit == null) throw new IllegalArgumentException("Habit is required");
    if (duration == null || duration < 0) throw new IllegalArgumentException("Duration must be non-negative");
    RoutineActivity newRoutineActivity = new RoutineActivity();
    newRoutineActivity.setRoutine(routine);
    newRoutineActivity.setHabit(habit);
    newRoutineActivity.setDuration(duration);
    newRoutineActivity.setTargetTime(targetTime);
    newRoutineActivity.setNotes(notes);
    return routineActivityRepository.save(newRoutineActivity);
    }

    @Transactional
    public RoutineActivity update(Long id, Integer duration, LocalTime targetTime, String notes) {
        RoutineActivity existing = findById(id);
        if (existing == null) throw new RuntimeException("RoutineActivity not found");
        if (duration != null) existing.setDuration(duration);
        if (targetTime != null) existing.setTargetTime(targetTime);
        if (notes != null) existing.setNotes(notes);
        return routineActivityRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (routineActivityRepository.existsById(id)) { routineActivityRepository.deleteById(id); return true; }
        return false;
    }
}
