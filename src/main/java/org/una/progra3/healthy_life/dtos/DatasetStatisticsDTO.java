package org.una.progra3.healthy_life.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas del dataset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetStatisticsDTO {
    private Integer users;
    private Integer habits;
    private Integer routines;
    private Integer guides;
    private Integer reminders;
    private Integer completedActivities;
    private Integer progressLogs;
    private Integer roles;
}