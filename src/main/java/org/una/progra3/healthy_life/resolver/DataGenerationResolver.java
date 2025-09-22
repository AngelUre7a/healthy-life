package org.una.progra3.healthy_life.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.una.progra3.healthy_life.dtos.DatasetGenerationResponseDTO;
import org.una.progra3.healthy_life.dtos.DatasetStatisticsDTO;
import org.una.progra3.healthy_life.service.DataGenerationService;
import org.una.progra3.healthy_life.repository.*;

/**
 * GraphQL Resolver for Data Generation operations
 * IMPORTANTE: Solo usar en ambiente de desarrollo/testing
 */
@Controller
public class DataGenerationResolver {

    @Autowired
    private DataGenerationService dataGenerationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private CompletedActivityRepository completedActivityRepository;

    @Autowired
    private ProgressLogRepository progressLogRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MutationMapping
    public DatasetGenerationResponseDTO generateMassiveDataset() {
        try {
            dataGenerationService.generateMassiveDataset();
            return new DatasetGenerationResponseDTO(
                true, 
                "Dataset masivo generado exitosamente. Más de 500,000 registros creados."
            );
        } catch (Exception e) {
            return new DatasetGenerationResponseDTO(
                false, 
                "Error al generar dataset masivo: " + e.getMessage()
            );
        }
    }

    @MutationMapping
    public DatasetGenerationResponseDTO cleanGeneratedData() {
        try {
            dataGenerationService.cleanGeneratedData();
            return new DatasetGenerationResponseDTO(
                true, 
                "Datos generados limpiados exitosamente."
            );
        } catch (Exception e) {
            return new DatasetGenerationResponseDTO(
                false, 
                "Error al limpiar datos: " + e.getMessage()
            );
        }
    }

    @MutationMapping
    public DatasetStatisticsDTO getDatasetStatistics() {
        return new DatasetStatisticsDTO(
            (int) userRepository.count(),
            (int) habitRepository.count(),
            (int) routineRepository.count(),
            (int) guideRepository.count(),
            (int) reminderRepository.count(),
            (int) completedActivityRepository.count(),
            (int) progressLogRepository.count(),
            (int) roleRepository.count()
        );
    }
}