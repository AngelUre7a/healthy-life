package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.repository.RoutineRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    public List<Routine> findAll() { return routineRepository.findAll(); }
    public Routine findById(Long id) { return routineRepository.findById(id).orElse(null); }
    public List<Routine> findByUser(User user) { return routineRepository.findByUser(user); }

    @Transactional
    public Routine create(Routine routine) {
        if (routine.getTitle() == null || routine.getTitle().isBlank()) throw new IllegalArgumentException("Routine title is required");
        if (routine.getUser() == null) throw new IllegalArgumentException("Routine user is required");
        return routineRepository.save(routine);
    }

    @Transactional
    public Routine update(Long id, String title, String daysOfWeek) {
        Routine existing = findById(id);
        if (existing == null) throw new RuntimeException("Routine not found");
        if (title != null) existing.setTitle(title);
        if (daysOfWeek != null) existing.setDaysOfWeek(daysOfWeek);
        return routineRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (routineRepository.existsById(id)) { routineRepository.deleteById(id); return true; }
        return false;
    }
}
