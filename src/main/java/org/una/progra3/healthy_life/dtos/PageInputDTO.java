package org.una.progra3.healthy_life.dtos;

/**
 * DTO para recibir parámetros de paginación en las consultas
 */
public class PageInputDTO {
    private int page = 0; // Página actual (0-indexed)
    private int size = 20; // Tamaño de página por defecto
    private String sortBy = "id"; // Campo por el cual ordenar
    private String sortDirection = "ASC"; // ASC o DESC

    public PageInputDTO() {}

    public PageInputDTO(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, Math.min(100, size)); // Limitar entre 1 y 100
    }

    public PageInputDTO(int page, int size, String sortBy, String sortDirection) {
        this(page, size);
        this.sortBy = sortBy != null ? sortBy : "id";
        this.sortDirection = sortDirection != null && 
                           (sortDirection.equalsIgnoreCase("DESC") || sortDirection.equalsIgnoreCase("ASC")) 
                           ? sortDirection.toUpperCase() : "ASC";
    }

    // Getters and Setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.max(1, Math.min(100, size)); // Limitar entre 1 y 100
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "id";
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection != null && 
                           (sortDirection.equalsIgnoreCase("DESC") || sortDirection.equalsIgnoreCase("ASC")) 
                           ? sortDirection.toUpperCase() : "ASC";
    }
}