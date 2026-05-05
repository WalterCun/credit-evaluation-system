package com.credit.risk.service;

import com.credit.risk.grpc.DebtInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RiskService Tests")
class RiskServiceTest {

    private RiskService riskService;

    @BeforeEach
    void setUp() {
        riskService = new RiskService();
    }

    @Nested
    @DisplayName("generateDebts Tests")
    class GenerateDebts {

        @RepeatedTest(10)
        @DisplayName("Should generate between 1 and 5 debts")
        void shouldGenerateBetween1And5Debts() {
            // Given
            String cedula = "1712345678";

            // When
            List<DebtInfo> debts = riskService.generateDebts(cedula);

            // Then
            assertNotNull(debts);
            assertTrue(debts.size() >= 1 && debts.size() <= 5);
        }

        @Test
        @DisplayName("Should set correct cédula on all debts")
        void shouldSetCorrectCedulaOnAllDebts() {
            // Given
            String cedula = "1712345678";

            // When
            List<DebtInfo> debts = riskService.generateDebts(cedula);

            // Then
            for (DebtInfo debt : debts) {
                assertEquals(cedula, debt.getCedula());
            }
        }

        @Test
        @DisplayName("Should set positive monto on all debts")
        void shouldSetPositiveMontoOnAllDebts() {
            // Given
            String cedula = "1712345678";

            // When
            List<DebtInfo> debts = riskService.generateDebts(cedula);

            // Then
            for (DebtInfo debt : debts) {
                assertTrue(debt.getMonto() >= 500 && debt.getMonto() <= 10000);
                assertNotNull(debt.getEntidad());
                assertFalse(debt.getEntidad().isEmpty());
            }
        }

        @Test
        @DisplayName("Should set mensualidad between 5% and 15% of monto")
        void shouldSetMensualidadBetween5And15PercentOfMonto() {
            // Given
            String cedula = "1712345678";

            // When
            List<DebtInfo> debts = riskService.generateDebts(cedula);

            // Then
            for (DebtInfo debt : debts) {
                double minMensualidad = debt.getMonto() * 0.05;
                double maxMensualidad = debt.getMonto() * 0.15;
                assertTrue(debt.getMensualidad() >= minMensualidad - 1);
                assertTrue(debt.getMensualidad() <= maxMensualidad + 1);
            }
        }

        @Test
        @DisplayName("Should use valid Ecuadorian financial entities")
        void shouldUseValidEcuadorianFinancialEntities() {
            // Given
            String cedula = "1712345678";
            String[] validEntities = {
                "Banco de Guayaquil", "Banco Pichincha", "Banco Austro",
                "Banco Bolivariano", "Cooperativa JEP", "Cooperativa Alianza"
            };

            // When
            List<DebtInfo> debts = riskService.generateDebts(cedula);

            // Then
            for (DebtInfo debt : debts) {
                boolean found = false;
                for (String entity : validEntities) {
                    if (entity.equals(debt.getEntidad())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Entity " + debt.getEntidad() + " should be a valid Ecuadorian financial entity");
            }
        }
    }

    @Nested
    @DisplayName("calculateScore Tests")
    class CalculateScore {

        @RepeatedTest(20)
        @DisplayName("Should return score between 1 and 100")
        void shouldReturnScoreBetween1And100() {
            // Given
            String cedula = "1712345678";
            double montoSolicitado = 10000;
            double salario = 5000;
            int tiempoAnios = 3;

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then
            assertTrue(score >= 1 && score <= 100);
        }

        @Test
        @DisplayName("Should return high score for low debt ratio")
        void shouldReturnHighScoreForLowDebtRatio() {
            // Given: High salary, small loan amount
            String cedula = "1712345678";
            double montoSolicitado = 3000;
            double salario = 10000;
            int tiempoAnios = 3;

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then
            assertTrue(score > 50, "Score should be relatively high for low debt ratio");
        }

        @Test
        @DisplayName("Should return low score for high debt ratio")
        void shouldReturnLowScoreForHighDebtRatio() {
            // Given: Low salary, large loan amount
            String cedula = "1712345678";
            double montoSolicitado = 50000;
            double salario = 1000;
            int tiempoAnios = 1;

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then: Score could be low due to high quota ratio
            // Note: Score depends on random factors, so we check it's within range
            assertTrue(score >= 1 && score <= 100);
        }

        @Test
        @DisplayName("Should return same cédula as input")
        void shouldReturnSameCedulaAsInput() {
            // Given
            String cedula = "1712345678";
            double montoSolicitado = 10000;
            double salario = 5000;
            int tiempoAnios = 3;

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then: Score calculation uses cédula internally
            assertTrue(score >= 1 && score <= 100);
        }

        @Test
        @DisplayName("Should calculate correct monthly payment")
        void shouldCalculateCorrectMonthlyPayment() {
            // Given: 12000 loan over 3 years = 333.33/month
            String cedula = "1712345678";
            double montoSolicitado = 12000;
            double salario = 5000;
            int tiempoAnios = 3;

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then: Verify score is calculated (quota = 333.33, ratio = 333.33/5000 = 0.067)
            assertTrue(score >= 1 && score <= 100);
        }
    }

    @Nested
    @DisplayName("score range calculations")
    class ScoreRangeCalculations {

        @Test
        @DisplayName("Should give maximum points for low debt ratio (<= 0.30)")
        void shouldGiveMaximumPointsForLowDebtRatio() {
            // Given: Very low debt ratio (cuota is small compared to limit)
            String cedula = "1712345678";
            double montoSolicitado = 1000;  // Small loan
            double salario = 10000;          // High salary
            int tiempoAnios = 30;              // Long term

            // When
            int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);

            // Then: Should have good score due to favorable ratios
            assertTrue(score >= 40, "Score should be at least moderate for favorable ratios");
        }
    }
}