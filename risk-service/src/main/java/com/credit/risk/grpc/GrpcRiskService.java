package com.credit.risk.grpc;

import java.time.Duration;

import com.credit.risk.service.RiskService;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@GrpcService
public class GrpcRiskService implements RiskGrpc {
    private static final Logger LOG = Logger.getLogger(GrpcRiskService.class);

    @Inject
    RiskService riskService;

    @Override
    public Uni<RiskScoreResponse> getScore(RiskRequest request) {
        String cedula = request.getCedula();
        double montoSolicitado = request.getMontoSolicitado();
        double salario = request.getSalario();
        int tiempoAnios = request.getTiempoAnios();

        LOG.infov("gRPC GetScore cedula={0} monto={1} salario={2} plazo={3}",
            cedula, montoSolicitado, salario, tiempoAnios);

        int score = riskService.calculateScore(cedula, montoSolicitado, salario, tiempoAnios);
        LOG.infov("Score calculado cedula={0} score={1}", cedula, score);

        return Uni.createFrom().item(
            RiskScoreResponse.newBuilder()
                .setCedula(cedula)
                .setScore(score)
                .build()
        ).onItem().delayIt().by(Duration.ofMillis(2000));
    }

    @Override
    public Uni<DebtsResponse> getDebts(RiskRequest request) {
        String cedula = request.getCedula();
        LOG.infov("gRPC GetDebts cedula={0}", cedula);

        return Uni.createFrom().item(cedula)
            .onItem().delayIt().by(Duration.ofMillis(1500))
            .map(c -> {
                var debts = riskService.generateDebts(c);
                DebtsResponse.Builder responseBuilder = DebtsResponse.newBuilder();
                for (var debt : debts) {
                    responseBuilder.addDebts(debt);
                }
                LOG.infov("Deudas generadas cedula={0} cantidad={1}", c, debts.size());
                return responseBuilder.build();
            });
    }
}
