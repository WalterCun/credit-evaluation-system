package com.credit.orchestrator.service;

import java.util.List;

import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.grpc.DebtsResponse;
import com.credit.risk.grpc.MutinyRiskGrpc;
import com.credit.risk.grpc.RiskRequest;
import com.credit.risk.grpc.RiskScoreResponse;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RiskClient {

    @GrpcClient("risk-service")
    MutinyRiskGrpc grpcStub;

    public Uni<RiskScoreResponse> getScore(String cedula) {
        return grpcStub.getScore(RiskRequest.newBuilder().setCedula(cedula).build());
    }

    public Uni<List<DebtInfo>> getDebts(String cedula) {
        return grpcStub.getDebts(RiskRequest.newBuilder().setCedula(cedula).build())
                .map(DebtsResponse::getDebtsList);
    }
}
