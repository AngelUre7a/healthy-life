package org.una.progra3.healthy_life.dtos;

/**
 * DTO para respuestas paginadas de usuarios
 */
public class UserPagedResponseDTO {
    private java.util.List<UserDTO> content;
    private PageInfoDTO pageInfo;

    public UserPagedResponseDTO() {}

    public UserPagedResponseDTO(java.util.List<UserDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    // Getters and Setters
    public java.util.List<UserDTO> getContent() {
        return content;
    }

    public void setContent(java.util.List<UserDTO> content) {
        this.content = content;
    }

    public PageInfoDTO getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoDTO pageInfo) {
        this.pageInfo = pageInfo;
    }
}