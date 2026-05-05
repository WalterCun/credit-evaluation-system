package com.credit.orchestrator.validator;

import com.credit.orchestrator.model.EvaluationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreditRequestValidator Tests")
class CreditRequestValidatorTest {

    private EvaluationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new EvaluationRequest();
        // Use a valid cédula: 0102345675 (province 01, valid checksum)
        validRequest.setCedula("0102345675");
        validRequest.setMontoSolicitado(new BigDecimal("10000"));
        validRequest.setSalario(new BigDecimal("5000"));
        validRequest.setTiempoAnios(3);
    }

    @Nested
    @DisplayName("Valid requests")
    class ValidRequests {

        @Test
        @DisplayName("Should return empty list for valid request")
        void shouldReturnEmptyListForValidRequest() {
            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.isEmpty(), "Valid request should have no errors");
        }

        @Test
        @DisplayName("Should accept minimum time 1 year")
        void shouldAcceptMinimumTime1Year() {
            // Given
            validRequest.setTiempoAnios(1);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("Should accept maximum time 30 years")
        void shouldAcceptMaximumTime30Years() {
            // Given
            validRequest.setTiempoAnios(30);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.isEmpty());
        }
    }

    @Nested
    @DisplayName("Invalid cédula")
    class InvalidCedula {

        @Test
        @DisplayName("Should return error for null cédula")
        void shouldReturnErrorForNullCedula() {
            // Given
            validRequest.setCedula(null);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertEquals(1, errors.size());
            assertEquals("Cédula ecuatoriana inválida (10 dígitos, módulo 10)", errors.get(0));
        }

        @Test
        @DisplayName("Should return error for invalid cédula")
        void shouldReturnErrorForInvalidCedula() {
            // Given
            validRequest.setCedula("1234567890");

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("Cédula ecuatoriana inválida (10 dígitos, módulo 10)"));
        }

        @Test
        @DisplayName("Should return error for short cédula")
        void shouldReturnErrorForShortCedula() {
            // Given
            validRequest.setCedula("171234567");

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("Cédula ecuatoriana inválida (10 dígitos, módulo 10)"));
        }
    }

    @Nested
    @DisplayName("Invalid monto solicitado")
    class InvalidMontoSolicitado {

        @Test
        @DisplayName("Should return error for null monto solicitado")
        void shouldReturnErrorForNullMontoSolicitado() {
            // Given
            validRequest.setMontoSolicitado(null);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertEquals(1, errors.size());
            assertTrue(errors.contains("El monto solicitado debe ser positivo"));
        }

        @Test
        @DisplayName("Should return error for zero monto solicitado")
        void shouldReturnErrorForZeroMontoSolicitado() {
            // Given
            validRequest.setMontoSolicitado(BigDecimal.ZERO);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El monto solicitado debe ser positivo"));
        }

        @Test
        @DisplayName("Should return error for negative monto solicitado")
        void shouldReturnErrorForNegativeMontoSolicitado() {
            // Given
            validRequest.setMontoSolicitado(new BigDecimal("-1000"));

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El monto solicitado debe ser positivo"));
        }
    }

    @Nested
    @DisplayName("Invalid salario")
    class InvalidSalario {

        @Test
        @DisplayName("Should return error for null salario")
        void shouldReturnErrorForNullSalario() {
            // Given
            validRequest.setSalario(null);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertEquals(1, errors.size());
            assertTrue(errors.contains("El salario debe ser positivo"));
        }

        @Test
        @DisplayName("Should return error for zero salario")
        void shouldReturnErrorForZeroSalario() {
            // Given
            validRequest.setSalario(BigDecimal.ZERO);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El salario debe ser positivo"));
        }

        @Test
        @DisplayName("Should return error for negative salario")
        void shouldReturnErrorForNegativeSalario() {
            // Given
            validRequest.setSalario(new BigDecimal("-5000"));

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El salario debe ser positivo"));
        }
    }

    @Nested
    @DisplayName("Invalid tiempo anios")
    class InvalidTiempoAnios {

        @Test
        @DisplayName("Should return error for null tiempo anios")
        void shouldReturnErrorForNullTiempoAnios() {
            // Given
            validRequest.setTiempoAnios(null);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertEquals(1, errors.size());
            assertTrue(errors.contains("El tiempo debe estar entre 1 y 30 años"));
        }

        @Test
        @DisplayName("Should return error for zero time")
        void shouldReturnErrorForZeroTime() {
            // Given
            validRequest.setTiempoAnios(0);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El tiempo debe estar entre 1 y 30 años"));
        }

        @Test
        @DisplayName("Should return error for time less than 1")
        void shouldReturnErrorForTimeLessThan1() {
            // Given
            validRequest.setTiempoAnios(-5);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El tiempo debe estar entre 1 y 30 años"));
        }

        @Test
        @DisplayName("Should return error for time greater than 30")
        void shouldReturnErrorForTimeGreaterThan30() {
            // Given
            validRequest.setTiempoAnios(35);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then
            assertTrue(errors.contains("El tiempo debe estar entre 1 y 30 años"));
        }
    }

    @Nested
    @DisplayName("Multiple errors")
    class MultipleErrors {

        @Test
        @DisplayName("Should return multiple errors for completely invalid request")
        void shouldReturnMultipleErrorsForInvalidRequest() {
            // Given
            EvaluationRequest invalidRequest = new EvaluationRequest();

            // When
            List<String> errors = CreditRequestValidator.validate(invalidRequest);

            // Then
            assertEquals(4, errors.size());
            assertTrue(errors.contains("Cédula ecuatoriana inválida (10 dígitos, módulo 10)"));
            assertTrue(errors.contains("El monto solicitado debe ser positivo"));
            assertTrue(errors.contains("El salario debe ser positivo"));
            assertTrue(errors.contains("El tiempo debe estar entre 1 y 30 años"));
        }

        @Test
        @DisplayName("Should return multiple specific errors")
        void shouldReturnMultipleSpecificErrors() {
            // Given
            validRequest.setMontoSolicitado(null);
            validRequest.setSalario(null);

            // When
            List<String> errors = CreditRequestValidator.validate(validRequest);

            // Then - cédula is valid, so only 2 errors expected
            assertEquals(2, errors.size());
        }
    }
}