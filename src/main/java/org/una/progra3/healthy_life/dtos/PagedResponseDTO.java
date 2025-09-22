package org.una.progra3.healthy_life.dtos;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas
 */
public class PagedResponseDTO<T> {
    private List<T> content;
    private PageInfoDTO pageInfo;

    public PagedResponseDTO() {}

    public PagedResponseDTO(List<T> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageInfoDTO getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDTO pageInfo) {
        this.pageInfo = pageInfo;
    }
}