package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.Guide;
import org.una.progra3.healthy_life.entity.enums.HabitCategory;
import org.una.progra3.healthy_life.repository.GuideRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GuideService {

    @Autowired
    private GuideRepository guideRepository;

    public List<Guide> findAll() { return guideRepository.findAll(); }
    public Guide findById(Long id) { return guideRepository.findById(id).orElse(null); }
    public List<Guide> findByCategory(HabitCategory category) { return guideRepository.findByCategory(category); }

    @Transactional
    public Guide create(Guide guide) {
        if (guide.getTitle() == null || guide.getTitle().isBlank()) throw new IllegalArgumentException("Guide title is required");
        return guideRepository.save(guide);
    }

    @Transactional
    public Guide update(Long id, String title, String content, HabitCategory category) {
        Guide existing = findById(id);
        if (existing == null) throw new RuntimeException("Guide not found");
        if (title != null) existing.setTitle(title);
        if (content != null) existing.setContent(content);
        if (category != null) existing.setCategory(category);
        return guideRepository.save(existing);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (guideRepository.existsById(id)) { guideRepository.deleteById(id); return true; }
        return false;
    }
}
