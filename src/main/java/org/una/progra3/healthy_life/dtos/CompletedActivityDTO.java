package org.una.progra3.healthy_life.dtos;

import java.time.LocalDateTime;

public class CompletedActivityDTO {
    private Long id;
    private Long habitId;
    private Long progressLogId;
    private LocalDateTime completedAt;
    private String notes;

    public CompletedActivityDTO() {}

    public Long getId() {
         return id; 
    }

    public void setId(Long id) {
         this.id = id; 
    }

    public Long getHabitId() {
         return habitId; 
    }
    public void setHabitId(Long habitId) {
         this.habitId = habitId; 
    }

    public Long getProgressLogId() {
         return progressLogId; 
    }

    public void setProgressLogId(Long progressLogId) {
         this.progressLogId = progressLogId; 
    }

    public LocalDateTime getCompletedAt() {
         return completedAt; 
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
         this.completedAt = completedAt; 
    }

    public String getNotes() {
         return notes; 
    }

    public void setNotes(String notes) {
         this.notes = notes; 
    }
}
