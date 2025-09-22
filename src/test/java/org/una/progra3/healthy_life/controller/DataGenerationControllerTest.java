package org.una.progra3.healthy_life.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.una.progra3.healthy_life.service.DataGenerationService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Objects;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataGenerationControllerTest {

    @Mock
    DataGenerationService dataGenerationService;

    @InjectMocks
    DataGenerationController controller;

    @Test
    void generateMassiveDataset_success() {
    ResponseEntity<Map<String, String>> resp = controller.generateMassiveDataset();
    assertEquals(200, resp.getStatusCode().value());
    assertEquals("success", Objects.requireNonNull(resp.getBody()).get("status"));
        verify(dataGenerationService).generateMassiveDataset();
    }

    @Test
    void generateMassiveDataset_error() {
        doThrow(new RuntimeException("boom")).when(dataGenerationService).generateMassiveDataset();
    ResponseEntity<Map<String, String>> resp = controller.generateMassiveDataset();
    assertEquals(400, resp.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(resp.getBody()).get("status"));
    }

    @Test
    void getDatasetStatistics_success() {
    ResponseEntity<Map<String, String>> resp = controller.getDatasetStatistics();
    assertEquals(200, resp.getStatusCode().value());
    assertEquals("success", Objects.requireNonNull(resp.getBody()).get("status"));
        verify(dataGenerationService).printDatasetStatistics();
    }

    @Test
    void getDatasetStatistics_error() {
        doThrow(new RuntimeException("boom")).when(dataGenerationService).printDatasetStatistics();
    ResponseEntity<Map<String, String>> resp = controller.getDatasetStatistics();
    assertEquals(400, resp.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(resp.getBody()).get("status"));
    }

    @Test
    void cleanGeneratedData_success() {
    ResponseEntity<Map<String, String>> resp = controller.cleanGeneratedData();
    assertEquals(200, resp.getStatusCode().value());
    assertEquals("success", Objects.requireNonNull(resp.getBody()).get("status"));
        verify(dataGenerationService).cleanGeneratedData();
    }

    @Test
    void cleanGeneratedData_error() {
        doThrow(new RuntimeException("boom")).when(dataGenerationService).cleanGeneratedData();
    ResponseEntity<Map<String, String>> resp = controller.cleanGeneratedData();
    assertEquals(400, resp.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(resp.getBody()).get("status"));
    }

    @Test
    void generateUsers_limitsAndSuccess() {
        // over limit
    ResponseEntity<Map<String, String>> over = controller.generateUsers(100001);
    assertEquals(400, over.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(over.getBody()).get("status"));
        verify(dataGenerationService, never()).generateUsers(anyInt());

        // valid
    ResponseEntity<Map<String, String>> ok = controller.generateUsers(123);
    assertEquals(200, ok.getStatusCode().value());
    assertEquals("success", Objects.requireNonNull(ok.getBody()).get("status"));
        verify(dataGenerationService).generateUsers(123);
    }

    @Test
    void generateUsers_errorFromService() {
        doThrow(new RuntimeException("x")).when(dataGenerationService).generateUsers(10);
    ResponseEntity<Map<String, String>> resp = controller.generateUsers(10);
    assertEquals(400, resp.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(resp.getBody()).get("status"));
    }

    @Test
    void generateHabits_limitsAndSuccess() {
        // over limit
    ResponseEntity<Map<String, String>> over = controller.generateHabits(50001);
    assertEquals(400, over.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(over.getBody()).get("status"));
        verify(dataGenerationService, never()).generateHabits(anyInt());

        // valid
    ResponseEntity<Map<String, String>> ok = controller.generateHabits(321);
    assertEquals(200, ok.getStatusCode().value());
    assertEquals("success", Objects.requireNonNull(ok.getBody()).get("status"));
        verify(dataGenerationService).generateHabits(321);
    }

    @Test
    void generateHabits_errorFromService() {
        doThrow(new RuntimeException("x")).when(dataGenerationService).generateHabits(9);
    ResponseEntity<Map<String, String>> resp = controller.generateHabits(9);
    assertEquals(400, resp.getStatusCode().value());
    assertEquals("error", Objects.requireNonNull(resp.getBody()).get("status"));
    }
}
