package com.credit.orchestrator.service;

import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.grpc.DebtsResponse;
import com.credit.risk.grpc.RiskGrpc;
import com.credit.risk.grpc.RiskRequest;
import com.credit.risk.grpc.RiskScoreResponse;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Cliente gRPC para comunicarse con el risk-service.
 *
 * Utiliza el stub generado por Quarkus gRPC a partir de risk_service.proto.
 * Los métodos retornan Uni (reactivo) para integrarse con el pipeline reactivo del orchestrator.
 *
 * Conexión configurada en application.properties:
 *   quarkus.grpc.clients.risk-service.host=risk-service
 *   quarkus.grpc.clients.risk-service.port=9000
 *
 * Métodos expuestos:
 *   - getScore(EvaluationRequest) → RiskScoreResponse (score 1-100)
 *   - getDebts(String cedula)     → List<DebtInfo> (deudas existentes del solicitante)
 */
@ApplicationScoped
public class RiskClient {

    /** Stub gRPC generado por Quarkus a partir del proto. Se inyecta automáticamente. */
    @GrpcClient("risk-service")
    RiskGrpc grpcStub;

    /**
     * Solicita el score de riesgo al risk-service para una solicitud de crédito.
     *
     * @param request Datos completos de la solicitud (necesarios para el cálculo del score).
     * @return Uni con RiskScoreResponse conteniendo la cédula y el score calculado.
     */
    public Uni<RiskScoreResponse> getScore(EvaluationRequest request) {
        RiskRequest grpcRequest = RiskRequest.newBuilder()
                .setCedula(request.getCedula())
                .setMontoSolicitado(request.getMontoSolicitado().doubleValue())
                .setSalario(request.getSalario().doubleValue())
                .setTiempoAnios(request.getTiempoAnios())
                .build();
        return grpcStub.getScore(grpcRequest);
    }

    /**
     * Solicita las deudas existentes del solicitante al risk-service.
     *
     * @param cedula Cédula de identidad del solicitante.
     * @return Uni con lista de DebtInfo (entidad, monto total, mensualidad).
     */
    public Uni<List<DebtInfo>> getDebts(String cedula) {
        return grpcStub.getDebts(RiskRequest.newBuilder().setCedula(cedula).build())
                .map(DebtsResponse::getDebtsList);
    }
}
