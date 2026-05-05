package com.credit.risk.grpc;

import com.credit.risk.service.RiskService;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GrpcRiskService Tests")
class GrpcRiskServiceTest {

    @Mock
    RiskService riskService;

    @InjectMocks
    GrpcRiskService grpcRiskService;

    private RiskRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = RiskRequest.newBuilder()
                .setCedula("1712345678")
                .setMontoSolicitado(10000.0)
                .setSalario(5000.0)
                .setTiempoAnios(3)
                .build();
    }

    @Nested
    @DisplayName("getScore Tests")
    class GetScore {

        @Test
        @DisplayName("Should return RiskScoreResponse with correct cédula and score")
        void shouldReturnRiskScoreResponseWithCorrectCedulaAndScore() {
            // Given
            int expectedScore = 75;
            when(riskService.calculateScore(anyString(), anyDouble(), anyDouble(), anyInt()))
                    .thenReturn(expectedScore);

            // When
            Uni<RiskScoreResponse> result = grpcRiskService.getScore(validRequest);

            // Then
            RiskScoreResponse response = result.await().indefinitely();
            assertEquals("1712345678", response.getCedula());
            assertEquals(expectedScore, response.getScore());
        }

        @Test
        @DisplayName("Should call riskService.calculateScore with correct parameters")
        void shouldCallRiskServiceCalculateScoreWithCorrectParameters() {
            // Given
            when(riskService.calculateScore(anyString(), anyDouble(), anyDouble(), anyInt()))
                    .thenReturn(75);

            // When
            grpcRiskService.getScore(validRequest).await().indefinitely();

            // Then
            verify(riskService).calculateScore(
                    eq("1712345678"),
                    eq(10000.0),
                    eq(5000.0),
                    eq(3)
            );
        }

        @Test
        @DisplayName("Should return score 100 when service returns 100")
        void shouldReturnScore100WhenServiceReturns100() {
            // Given
            when(riskService.calculateScore(anyString(), anyDouble(), anyDouble(), anyInt()))
                    .thenReturn(100);

            // When
            RiskScoreResponse response = grpcRiskService.getScore(validRequest).await().indefinitely();

            // Then
            assertEquals(100, response.getScore());
        }

        @Test
        @DisplayName("Should return score 1 when service returns 1")
        void shouldReturnScore1WhenServiceReturns1() {
            // Given
            when(riskService.calculateScore(anyString(), anyDouble(), anyDouble(), anyInt()))
                    .thenReturn(1);

            // When
            RiskScoreResponse response = grpcRiskService.getScore(validRequest).await().indefinitely();

            // Then
            assertEquals(1, response.getScore());
        }

        @Test
        @DisplayName("Should delay response by 2 seconds")
        void shouldDelayResponseBy2Seconds() {
            // Given
            when(riskService.calculateScore(anyString(), anyDouble(), anyDouble(), anyInt()))
                    .thenReturn(75);

            long startTime = System.currentTimeMillis();

            // When
            grpcRiskService.getScore(validRequest).await().indefinitely();

            long elapsedTime = System.currentTimeMillis() - startTime;

            // Then: Should have at least 2000ms delay (with some tolerance for test execution)
            assertTrue(elapsedTime >= 1900, "Response should be delayed by approximately 2 seconds, but took " + elapsedTime + "ms");
        }
    }

    @Nested
    @DisplayName("getDebts Tests")
    class GetDebts {

        @Test
        @DisplayName("Should return DebtsResponse with generated debts")
        void shouldReturnDebtsResponseWithGeneratedDebts() {
            // Given
            List<DebtInfo> mockDebts = List.of(
                    DebtInfo.newBuilder()
                            .setCedula("1712345678")
                            .setEntidad("Banco Pichincha")
                            .setMonto(5000.0)
                            .setMensualidad(450.0)
                            .build()
            );
            when(riskService.generateDebts(anyString())).thenReturn(mockDebts);

            // When
            Uni<DebtsResponse> result = grpcRiskService.getDebts(validRequest);

            // Then
            DebtsResponse response = result.await().indefinitely();
            assertEquals(1, response.getDebtsList().size());
            assertEquals("1712345678", response.getDebtsList().get(0).getCedula());
        }

        @Test
        @DisplayName("Should call riskService.generateDebts with correct cédula")
        void shouldCallRiskServiceGenerateDebtsWithCorrectCedula() {
            // Given
            when(riskService.generateDebts(anyString())).thenReturn(List.of());

            // When
            grpcRiskService.getDebts(validRequest).await().indefinitely();

            // Then
            verify(riskService).generateDebts("1712345678");
        }

        @Test
        @DisplayName("Should return empty debts list when service returns empty")
        void shouldReturnEmptyDebtsListWhenServiceReturnsEmpty() {
            // Given
            when(riskService.generateDebts(anyString())).thenReturn(List.of());

            // When
            DebtsResponse response = grpcRiskService.getDebts(validRequest).await().indefinitely();

            // Then
            assertEquals(0, response.getDebtsList().size());
        }

        @Test
        @DisplayName("Should return multiple debts when service returns multiple")
        void shouldReturnMultipleDebtsWhenServiceReturnsMultiple() {
            // Given
            List<DebtInfo> mockDebts = List.of(
                    DebtInfo.newBuilder()
                            .setCedula("1712345678")
                            .setEntidad("Banco Pichincha")
                            .setMonto(5000.0)
                            .setMensualidad(450.0)
                            .build(),
                    DebtInfo.newBuilder()
                            .setCedula("1712345678")
                            .setEntidad("Cooperativa JEP")
                            .setMonto(1200.0)
                            .setMensualidad(150.0)
                            .build()
            );
            when(riskService.generateDebts(anyString())).thenReturn(mockDebts);

            // When
            DebtsResponse response = grpcRiskService.getDebts(validRequest).await().indefinitely();

            // Then
            assertEquals(2, response.getDebtsList().size());
        }

        @Test
        @DisplayName("Should delay response by 1.5 seconds")
        void shouldDelayResponseBy1_5Seconds() {
            // Given
            when(riskService.generateDebts(anyString())).thenReturn(List.of());

            long startTime = System.currentTimeMillis();

            // When
            grpcRiskService.getDebts(validRequest).await().indefinitely();

            long elapsedTime = System.currentTimeMillis() - startTime;

            // Then: Should have at least 1500ms delay
            assertTrue(elapsedTime >= 1400, "Response should be delayed by approximately 1.5 seconds, but took " + elapsedTime + "ms");
        }
    }
}