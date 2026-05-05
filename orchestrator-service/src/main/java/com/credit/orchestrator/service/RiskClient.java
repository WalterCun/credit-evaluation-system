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

@ApplicationScoped
public class RiskClient {

    @GrpcClient("risk-service")
    RiskGrpc grpcStub;

    public Uni<RiskScoreResponse> getScore(EvaluationRequest request) {
        RiskRequest grpcRequest = RiskRequest.newBuilder()
            .setCedula(request.getCedula())
            .setMontoSolicitado(request.getMontoSolicitado().doubleValue())
            .setSalario(request.getSalario().doubleValue())
            .setTiempoAnios(request.getTiempoAnios())
            .build();
        return grpcStub.getScore(grpcRequest);
    }

    public Uni<List<DebtInfo>> getDebts(String cedula) {
        return grpcStub.getDebts(RiskRequest.newBuilder().setCedula(cedula).build())
            .map(DebtsResponse::getDebtsList);
    }
}
