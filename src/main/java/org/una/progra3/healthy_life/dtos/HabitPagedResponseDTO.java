package org.una.progra3.healthy_life.dtos;

/**
 * DTO para respuestas paginadas de hábitos
 */
public class HabitPagedResponseDTO {
    private java.util.List<HabitDTO> content;
    private PageInfoDTO pageInfo;

    public HabitPagedResponseDTO() {}

    public HabitPagedResponseDTO(java.util.List<HabitDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    // Getters and Setters
    public java.util.List<HabitDTO> getContent() {
        return content;
    }

    public void setContent(java.util.List<HabitDTO> content) {
        this.content = content;
    }

    public PageInfoDTO getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDTO pageInfo) {
        this.pageInfo = pageInfo;
    }
}