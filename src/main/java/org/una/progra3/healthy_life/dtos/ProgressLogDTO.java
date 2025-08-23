package org.una.progra3.healthy_life.dtos;

import java.time.LocalDate;
import java.util.List;

public class ProgressLogDTO {
    private Long id;
    private Long userId;
    private Long routineId;
    private LocalDate date;
    private List<Long> completedActivityIds;

    public ProgressLogDTO() {}

    public Long getId() {
         return id; 
    }

    public void setId(Long id) {
         this.id = id; 
    }

    public Long getUserId() {
         return userId; 
    }

    public void setUserId(Long userId) {
         this.userId = userId; 
        }

    public Long getRoutineId() {
         return routineId; 
    }

    public void setRoutineId(Long routineId) {
         this.routineId = routineId; 
    }

    public LocalDate getDate() {
        return date; 
    }

    public void setDate(LocalDate date) {
         this.date = date; 
    }

    public List<Long> getCompletedActivityIds() {
         return completedActivityIds; 
    }

    public void setCompletedActivityIds(List<Long> completedActivityIds) {
         this.completedActivityIds = completedActivityIds; 
    }
}
