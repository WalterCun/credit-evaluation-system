package com.credit.orchestrator.service;

import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.grpc.DebtsResponse;
import com.credit.risk.grpc.RiskGrpc;
import com.credit.risk.grpc.RiskRequest;
import com.credit.risk.grpc.RiskScoreResponse;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskClient Tests")
class RiskClientTest {

    @Mock
    RiskGrpc riskGrpc;

    @InjectMocks
    RiskClient riskClient;

    private EvaluationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new EvaluationRequest();
        validRequest.setCedula("1712345678");
        validRequest.setMontoSolicitado(new BigDecimal("10000"));
        validRequest.setSalario(new BigDecimal("5000"));
        validRequest.setTiempoAnios(3);
    }

    @Nested
    @DisplayName("getScore Tests")
    class GetScoreTests {

        @Test
        @DisplayName("Should return RiskScoreResponse with correct data")
        void shouldReturnRiskScoreResponseWithCorrectData() {
            // Given
            RiskScoreResponse expectedResponse = RiskScoreResponse.newBuilder()
                    .setCedula("1712345678")
                    .setScore(75)
                    .build();

            when(riskGrpc.getScore(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            Uni<RiskScoreResponse> result = riskClient.getScore(validRequest);

            // Then
            RiskScoreResponse response = result.await().indefinitely();
            assertEquals("1712345678", response.getCedula());
            assertEquals(75, response.getScore());
        }

        @Test
        @DisplayName("Should call gRPC stub with correct RiskRequest")
        void shouldCallGrpcStubWithCorrectRiskRequest() {
            // Given
            RiskScoreResponse expectedResponse = RiskScoreResponse.newBuilder()
                    .setCedula("1712345678")
                    .setScore(75)
                    .build();

            when(riskGrpc.getScore(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            riskClient.getScore(validRequest).await().indefinitely();

            // Then
            ArgumentCaptor<RiskRequest> captor = ArgumentCaptor.forClass(RiskRequest.class);
            verify(riskGrpc).getScore(captor.capture());
            RiskRequest capturedRequest = captor.getValue();
            assertEquals("1712345678", capturedRequest.getCedula());
            assertEquals(10000.0, capturedRequest.getMontoSolicitado());
            assertEquals(5000.0, capturedRequest.getSalario());
            assertEquals(3, capturedRequest.getTiempoAnios());
        }

        @Test
        @DisplayName("Should propagate error from gRPC service")
        void shouldPropagateErrorFromGrpcService() {
            // Given
            RuntimeException grpcException = new RuntimeException("gRPC connection failed");
            when(riskGrpc.getScore(any(RiskRequest.class))).thenReturn(Uni.createFrom().failure(grpcException));

            // When/Then
            assertThrows(RuntimeException.class, () -> riskClient.getScore(validRequest).await().indefinitely());
        }
    }

    @Nested
    @DisplayName("getDebts Tests")
    class GetDebtsTests {

        @Test
        @DisplayName("Should return list of DebtInfo")
        void shouldReturnListOfDebtInfo() {
            // Given
            List<DebtInfo> expectedDebts = List.of(
                    DebtInfo.newBuilder()
                            .setCedula("1712345678")
                            .setEntidad("Banco Pichincha")
                            .setMonto(5000.0)
                            .setMensualidad(450.0)
                            .build()
            );
            DebtsResponse expectedResponse = DebtsResponse.newBuilder()
                    .addAllDebts(expectedDebts)
                    .build();

            when(riskGrpc.getDebts(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            List<DebtInfo> result = riskClient.getDebts("1712345678").await().indefinitely();

            // Then
            assertEquals(1, result.size());
            assertEquals("1712345678", result.get(0).getCedula());
            assertEquals("Banco Pichincha", result.get(0).getEntidad());
        }

        @Test
        @DisplayName("Should call gRPC with cédula in RiskRequest")
        void shouldCallGrpcWithCedulaInRiskRequest() {
            // Given
            DebtsResponse expectedResponse = DebtsResponse.newBuilder().build();
            when(riskGrpc.getDebts(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            riskClient.getDebts("1712345678").await().indefinitely();

            // Then
            ArgumentCaptor<RiskRequest> captor = ArgumentCaptor.forClass(RiskRequest.class);
            verify(riskGrpc).getDebts(captor.capture());
            assertEquals("1712345678", captor.getValue().getCedula());
        }

        @Test
        @DisplayName("Should return empty list when no debts")
        void shouldReturnEmptyListWhenNoDebts() {
            // Given
            DebtsResponse expectedResponse = DebtsResponse.newBuilder().build();
            when(riskGrpc.getDebts(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            List<DebtInfo> result = riskClient.getDebts("1712345678").await().indefinitely();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return multiple debts when present")
        void shouldReturnMultipleDebtsWhenPresent() {
            // Given
            List<DebtInfo> expectedDebts = List.of(
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
            DebtsResponse expectedResponse = DebtsResponse.newBuilder()
                    .addAllDebts(expectedDebts)
                    .build();

            when(riskGrpc.getDebts(any(RiskRequest.class))).thenReturn(Uni.createFrom().item(expectedResponse));

            // When
            List<DebtInfo> result = riskClient.getDebts("1712345678").await().indefinitely();

            // Then
            assertEquals(2, result.size());
        }
    }
}