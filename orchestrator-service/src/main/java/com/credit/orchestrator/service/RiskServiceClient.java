package com.credit.orchestrator.service;

import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.grpc.RiskScoreResponse;

import io.smallrye.mutiny.Uni;

/**
 * Interfaz para cliente de servicio de riesgo.
 * 
 * DIP: Abstracción que permite intercambiar implementaciones (gRPC, REST, mock).
 */
public interface RiskServiceClient {
    Uni<RiskScoreResponse> getScore(EvaluationRequest request);
    Uni<List<DebtInfo>> getDebts(String cedula);
}