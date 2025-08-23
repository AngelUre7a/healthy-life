package org.una.progra3.healthy_life.dtos;

import java.util.List;

public class RoutineDTO {
    private Long id;
    private String title;
    private Long userId;
    private String daysOfWeek;
    private List<Long> activityIds;

    public RoutineDTO() {}

    public Long getId() {
         return id; 
    }
    
    public void setId(Long id) {
         this.id = id; 
        }

    public String getTitle() {
         return title; 
        }

    public void setTitle(String title) {
         this.title = title; 
        }

    public Long getUserId() {
         return userId; 
        }

    public void setUserId(Long userId) {
         this.userId = userId; 
        }

    public String getDaysOfWeek() {
         return daysOfWeek; 
        }

    public void setDaysOfWeek(String daysOfWeek) {
         this.daysOfWeek = daysOfWeek; 
        }

    public List<Long> getActivityIds() {
         return activityIds; 
        }

    public void setActivityIds(List<Long> activityIds) {
         this.activityIds = activityIds; 
        }
}
