package com.credit.risk.grpc;

import java.time.Duration;

import com.credit.risk.service.RiskService;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

/**
 * Implementación gRPC del servicio de riesgo crediticio.
 *
 * Implementa la interfaz RiskGrpc generada por Quarkus a partir de risk_service.proto.
 * Expone dos métodos gRPC:
 *
 *   1. getScore(RiskRequest) → RiskScoreResponse
 *      Calcula el score de riesgo (1-100) para una solicitud de crédito.
 *      Incluye un delay simulado de 2 segundos para emular latencia de buró de crédito.
 *
 *   2. getDebts(RiskRequest) → DebtsResponse
 *      Obtiene las deudas existentes de un solicitante.
 *      Incluye un delay simulado de 1.5 segundos para emular latencia de consulta externa.
 *
 * Protocolo: gRPC sobre HTTP/2 (puerto 9000).
 * Los delays son simulados; en producción se reemplazarían por llamadas reales a burós de crédito.
 */
@GrpcService
public class GrpcRiskService implements RiskGrpc {
    private static final Logger LOG = Logger.getLogger(GrpcRiskService.class);

    @Inject
    RiskService riskService;

    /**
     * Calcula el score de riesgo crediticio vía gRPC.
     *
     * @param request RiskRequest con cédula, monto, salario y plazo.
     * @return Uni con RiskScoreResponse conteniendo la cédula y el score calculado.
     *
     * Ejemplo de request:
     *   { cedula: "1712345678", monto_solicitado: 10000.0, salario: 5000.0, tiempo_anios: 3 }
     *
     * Ejemplo de response:
     *   { cedula: "1712345678", score: 75 }
     */
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

    /**
     * Obtiene las deudas existentes de un solicitante vía gRPC.
     *
     * @param request RiskRequest con al menos la cédula del solicitante.
     * @return Uni con DebtsResponse conteniendo la lista de deudas.
     *
     * Ejemplo de response:
     *   {
     *     debts: [
     *       { cedula: "1712345678", entidad: "Banco Pichincha", monto: 5000.0, mensualidad: 450.0 },
     *       { cedula: "1712345678", entidad: "Cooperativa JEP", monto: 1200.0, mensualidad: 150.0 }
     *     ]
     *   }
     */
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
