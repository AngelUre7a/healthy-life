package org.una.progra3.healthy_life.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.una.progra3.healthy_life.service.DataGenerationService;

import java.util.Map;

/**
 * Controller para manejar la generación de datos masivos
 * IMPORTANTE: Solo usar en ambiente de desarrollo/testing
 */
@RestController
@RequestMapping("/api/data-generation")
public class DataGenerationController {

    @Autowired
    private DataGenerationService dataGenerationService;

    /**
     * Generar dataset completo de 500k+ registros
     */
    @PostMapping("/massive-dataset")
    public ResponseEntity<Map<String, String>> generateMassiveDataset() {
        try {
            dataGenerationService.generateMassiveDataset();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Dataset masivo generado exitosamente. Más de 500,000 registros creados."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al generar dataset: " + e.getMessage()
            ));
        }
    }

    /**
     * Obtener estadísticas del dataset actual
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, String>> getDatasetStatistics() {
        try {
            dataGenerationService.printDatasetStatistics();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Estadísticas del dataset impresas en la consola."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al obtener estadísticas: " + e.getMessage()
            ));
        }
    }

    /**
     * Limpiar datos generados (usar con precaución)
     */
    @DeleteMapping("/clean")
    public ResponseEntity<Map<String, String>> cleanGeneratedData() {
        try {
            dataGenerationService.cleanGeneratedData();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Datos generados limpiados exitosamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al limpiar datos: " + e.getMessage()
            ));
        }
    }

    /**
     * Generar solo usuarios (cantidad personalizada)
     */
    @PostMapping("/users/{count}")
    public ResponseEntity<Map<String, String>> generateUsers(@PathVariable int count) {
        try {
            if (count > 100000) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Máximo 100,000 usuarios por operación."
                ));
            }
            
            dataGenerationService.generateUsers(count);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", count + " usuarios generados exitosamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al generar usuarios: " + e.getMessage()
            ));
        }
    }

    /**
     * Generar solo hábitos (cantidad personalizada)
     */
    @PostMapping("/habits/{count}")
    public ResponseEntity<Map<String, String>> generateHabits(@PathVariable int count) {
        try {
            if (count > 50000) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Máximo 50,000 hábitos por operación."
                ));
            }
            
            dataGenerationService.generateHabits(count);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", count + " hábitos generados exitosamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "Error al generar hábitos: " + e.getMessage()
            ));
        }
    }
}