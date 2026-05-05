package com.credit.orchestrator.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvaluationResult Tests")
class EvaluationResultTest {

    @Test
    @DisplayName("Should convert CreditEvaluation to EvaluationResult correctly")
    void shouldConvertCreditEvaluationToEvaluationResult() {
        // Given
        CreditEvaluation entity = new CreditEvaluation();
        entity.setId(1L);
        entity.setCedula("1712345678");
        entity.setMontoSolicitado(new BigDecimal("10000.00"));
        entity.setSalario(new BigDecimal("5000.00"));
        entity.setTiempoAnios(3);
        entity.setScore(75);
        entity.setDeudaMensual(new BigDecimal("277.78"));
        entity.setEstado("APROBADO");
        OffsetDateTime now = OffsetDateTime.now();
        entity.setFechaEvaluacion(now);

        // When
        EvaluationResult result = EvaluationResult.fromEntity(entity);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("1712345678", result.getCedula());
        assertEquals(new BigDecimal("10000.00"), result.getMontoSolicitado());
        assertEquals(new BigDecimal("5000.00"), result.getSalario());
        assertEquals(3, result.getTiempoAnios());
        assertEquals(75, result.getScore());
        assertEquals(new BigDecimal("277.78"), result.getDeudaMensual());
        assertEquals("APROBADO", result.getEstado());
        assertEquals(now, result.getFechaEvaluacion());
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetAllFieldsCorrectly() {
        // Given
        EvaluationResult result = new EvaluationResult();
        OffsetDateTime now = OffsetDateTime.now();

        // When
        result.setId(1L);
        result.setCedula("1712345678");
        result.setMontoSolicitado(new BigDecimal("5000"));
        result.setSalario(new BigDecimal("3000"));
        result.setTiempoAnios(5);
        result.setScore(80);
        result.setDeudaMensual(new BigDecimal("200.50"));
        result.setEstado("RECHAZADO");
        result.setFechaEvaluacion(now);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("1712345678", result.getCedula());
        assertEquals(new BigDecimal("5000"), result.getMontoSolicitado());
        assertEquals(new BigDecimal("3000"), result.getSalario());
        assertEquals(5, result.getTiempoAnios());
        assertEquals(80, result.getScore());
        assertEquals(new BigDecimal("200.50"), result.getDeudaMensual());
        assertEquals("RECHAZADO", result.getEstado());
        assertEquals(now, result.getFechaEvaluacion());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        EvaluationResult result = new EvaluationResult();

        // When/Then - getters should not throw NPE
        assertNull(result.getId());
        assertNull(result.getCedula());
        assertNull(result.getMontoSolicitado());
        assertNull(result.getSalario());
        assertNull(result.getTiempoAnios());
        assertNull(result.getScore());
        assertNull(result.getDeudaMensual());
        assertNull(result.getEstado());
        assertNull(result.getFechaEvaluacion());
    }
}