package com.credit.orchestrator.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreditEvaluation Entity Tests")
class CreditEvaluationTest {

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetAllFieldsCorrectly() {
        // Given
        CreditEvaluation entity = new CreditEvaluation();
        OffsetDateTime now = OffsetDateTime.now();

        // When
        entity.setId(1L);
        entity.setCedula("1712345678");
        entity.setMontoSolicitado(new BigDecimal("10000.00"));
        entity.setSalario(new BigDecimal("5000.00"));
        entity.setTiempoAnios(3);
        entity.setScore(75);
        entity.setDeudaMensual(new BigDecimal("277.78"));
        entity.setEstado("APROBADO");
        entity.setFechaEvaluacion(now);

        // Then
        assertEquals(1L, entity.getId());
        assertEquals("1712345678", entity.getCedula());
        assertEquals(new BigDecimal("10000.00"), entity.getMontoSolicitado());
        assertEquals(new BigDecimal("5000.00"), entity.getSalario());
        assertEquals(3, entity.getTiempoAnios());
        assertEquals(75, entity.getScore());
        assertEquals(new BigDecimal("277.78"), entity.getDeudaMensual());
        assertEquals("APROBADO", entity.getEstado());
        assertEquals(now, entity.getFechaEvaluacion());
    }

    @Test
    @DisplayName("Should allow public field access")
    void shouldAllowPublicFieldAccess() {
        // Given
        CreditEvaluation entity = new CreditEvaluation();

        // When
        entity.id = 10L;
        entity.cedula = "1712345678";
        entity.montoSolicitado = new BigDecimal("10000");
        entity.salario = new BigDecimal("5000");
        entity.tiempoAnios = 3;
        entity.score = 75;
        entity.deudaMensual = new BigDecimal("277.78");
        entity.estado = "APROBADO";
        entity.fechaEvaluacion = OffsetDateTime.now();

        // Then
        assertEquals(10L, entity.id);
        assertEquals("1712345678", entity.cedula);
        assertEquals(new BigDecimal("10000"), entity.montoSolicitado);
        assertEquals(new BigDecimal("5000"), entity.salario);
        assertEquals(3, entity.tiempoAnios);
        assertEquals(75, entity.score);
        assertEquals(new BigDecimal("277.78"), entity.deudaMensual);
        assertEquals("APROBADO", entity.estado);
        assertNotNull(entity.fechaEvaluacion);
    }

    @Test
    @DisplayName("Should handle null and default values")
    void shouldHandleNullAndDefaultValues() {
        // Given
        CreditEvaluation entity = new CreditEvaluation();

        // When/Then
        assertNull(entity.getId());
        assertNull(entity.getCedula());
        assertNull(entity.getMontoSolicitado());
        assertNull(entity.getSalario());
        assertNull(entity.getTiempoAnios());
        assertNull(entity.getScore());
        assertNull(entity.getDeudaMensual());
        assertNull(entity.getEstado());
        assertNull(entity.getFechaEvaluacion());
    }
}