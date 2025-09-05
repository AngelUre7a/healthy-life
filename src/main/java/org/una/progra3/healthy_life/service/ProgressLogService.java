package org.una.progra3.healthy_life.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.una.progra3.healthy_life.entity.ProgressLog;
import org.una.progra3.healthy_life.entity.User;
import org.una.progra3.healthy_life.entity.Routine;
import org.una.progra3.healthy_life.repository.ProgressLogRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProgressLogService {

    @Autowired
    private ProgressLogRepository progressLogRepository;

    public List<ProgressLog> findAll() { return progressLogRepository.findAll(); }
    public ProgressLog findById(Long id) { return progressLogRepository.findById(id).orElse(null); }
    public List<ProgressLog> findByUser(User user) { return progressLogRepository.findByUser(user); }
    public List<ProgressLog> findByRoutine(Routine routine) { return progressLogRepository.findByRoutine(routine); }
    public List<ProgressLog> findByUserAndDate(User user, LocalDate date) { return progressLogRepository.findByUserAndDate(user, date); }

    @Transactional
    public ProgressLog create(ProgressLog log) {
        if (log.getUser() == null) throw new IllegalArgumentException("User is required");
        if (log.getDate() == null) throw new IllegalArgumentException("Date is required");
        return progressLogRepository.save(log);
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (progressLogRepository.existsById(id)) { progressLogRepository.deleteById(id); return true; }
        return false;
    }
}
