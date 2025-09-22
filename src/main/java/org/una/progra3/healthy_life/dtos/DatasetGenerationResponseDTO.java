package org.una.progra3.healthy_life.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de generación de dataset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetGenerationResponseDTO {
    private Boolean success;
    private String message;
}