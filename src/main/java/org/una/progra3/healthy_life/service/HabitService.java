package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Habit;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.HabitRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    public List<Habit> findAll() { return habitRepository.findAll(); }

    public Habit findById(Long id) { return habitRepository.findById(id).orElse(null); }

    public Habit findByName(String name) { return habitRepository.findByName(name).orElse(null); }

    public List<Habit> findByCategory(HabitCategory category) { return habitRepository.findByCategory(category); }

    @Transactional
    public Habit create(Habit habit) {
        if (habit.getName() == null || habit.getName().isBlank()) throw new IllegalArgumentException("Habit name is required");
        if (habitRepository.existsByName(habit.getName())) throw new RuntimeException("Habit name already exists");
        return habitRepository.save(habit);
    }

    @Transactional
    public Habit update(Long id, String name, HabitCategory category, String description) {
        Habit existing = findById(id);
        if (existing == null) throw new RuntimeException("Habit not found");
        if (name != null && !name.equals(existing.getName())) {
            if (habitRepository.existsByName(name)) throw new RuntimeException("Habit name already exists");
            existing.setName(name);
        }
        if (category != null) existing.setCategory(category);
        if (description != null) existing.setDescription(description);
        return habitRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (habitRepository.existsById(id)) { habitRepository.deleteById(id); return true; }
        return false;
    }
}
