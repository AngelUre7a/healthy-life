package org.una.progra3.healthy_life.dtos;

import java.util.Set;

public class GuideDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Set<Long> recommendedHabitIds;

    public GuideDTO() {}

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

    public String getContent() {
         return content; 
    }

    public void setContent(String content) {
         this.content = content; 
    }

    public String getCategory() {
         return category; 
    }

    public void setCategory(String category) {
         this.category = category; 
    }

    public Set<Long> getRecommendedHabitIds() {
         return recommendedHabitIds; 
    }

    public void setRecommendedHabitIds(Set<Long> recommendedHabitIds) {
         this.recommendedHabitIds = recommendedHabitIds; 
    }
}
