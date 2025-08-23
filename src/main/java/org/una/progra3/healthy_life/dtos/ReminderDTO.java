package org.una.progra3.healthy_life.dtos;

import java.time.LocalTime;

public class ReminderDTO {
    private Long id;
    private Long userId;
    private Long habitId;
    private LocalTime time;
    private String frequency;

    public ReminderDTO() {}

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

    public Long getHabitId() {
         return habitId; 
        }

    public void setHabitId(Long habitId) {
         this.habitId = habitId; 
        }

    public LocalTime getTime() {
         return time; 
        }

    public void setTime(LocalTime time) {
         this.time = time; 
        }

    public String getFrequency() {
         return frequency; 
        }

    public void setFrequency(String frequency) {
         this.frequency = frequency; 
        }
}
