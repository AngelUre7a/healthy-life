package org.una.progra3.healthy_life.dtos;

/**
 * DTO para respuestas paginadas de rutinas
 */
public class RoutinePagedResponseDTO {
    private java.util.List<RoutineDTO> content;
    private PageInfoDTO pageInfo;

    public RoutinePagedResponseDTO() {}

    public RoutinePagedResponseDTO(java.util.List<RoutineDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    // Getters and Setters
    public java.util.List<RoutineDTO> getContent() {
        return content;
    }

    public void setContent(java.util.List<RoutineDTO> content) {
        this.content = content;
    }

    public PageInfoDTO getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDTO pageInfo) {
        this.pageInfo = pageInfo;
    }
}