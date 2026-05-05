package com.credit.orchestrator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

import com.credit.orchestrator.model.CreditEvaluation;
import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.orchestrator.model.EvaluationResult;
import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.grpc.RiskScoreResponse;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreditEvaluationService {
    private static final Logger LOG = Logger.getLogger(CreditEvaluationService.class);

    @Inject
    RiskClient riskClient;

    @WithTransaction
    public Uni<EvaluationResult> evaluate(EvaluationRequest request) {
        LOG.debugv("Iniciando evaluación para cedula={0}", request.getCedula());
        Uni<RiskScoreResponse> scoreUni = riskClient.getScore(request.getCedula());
        Uni<List<DebtInfo>> debtsUni = riskClient.getDebts(request.getCedula());

        return Uni.combine().all().unis(scoreUni, debtsUni).asTuple()
                .flatMap(tuple -> {
                    int score = tuple.getItem1().getScore();
                    List<DebtInfo> debts = tuple.getItem2();

                    BigDecimal deudaMensual = debts.stream()
                            .map(d -> BigDecimal.valueOf(d.getMensualidad()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal cuotaMensual = request.getMontoSolicitado()
                            .divide(BigDecimal.valueOf(request.getTiempoAnios() * 12L), 2, RoundingMode.HALF_UP);

                    BigDecimal totalMensual = deudaMensual.add(cuotaMensual);
                    BigDecimal limiteEndeudamiento = request.getSalario().multiply(BigDecimal.valueOf(0.40));

                    String estado = (score > 70 && totalMensual.compareTo(limiteEndeudamiento) < 0)
                            ? "APROBADO" : "RECHAZADO";
                    LOG.infov("Resultado preliminar cedula={0} score={1} deudaMensual={2} cuotaNueva={3} estado={4}",
                            request.getCedula(), score, deudaMensual, cuotaMensual, estado);

                    CreditEvaluation evaluation = new CreditEvaluation();
                    evaluation.setCedula(request.getCedula());
                    evaluation.setMontoSolicitado(request.getMontoSolicitado());
                    evaluation.setSalario(request.getSalario());
                    evaluation.setTiempoAnios(request.getTiempoAnios());
                    evaluation.setScore(score);
                    evaluation.setDeudaMensual(deudaMensual);
                    evaluation.setEstado(estado);
                    evaluation.setFechaEvaluacion(OffsetDateTime.now());

                    return evaluation.persistAndFlush()
                            .replaceWith(EvaluationResult.fromEntity(evaluation))
                            .onFailure().invoke(error -> LOG.errorv(error,
                                    "Error persistiendo evaluación para cedula={0}", request.getCedula()));
                });
    }

    @WithTransaction
    public Uni<List<EvaluationResult>> listAll() {
        LOG.debug("Consultando historial de evaluaciones");
        return CreditEvaluation.<CreditEvaluation>listAll()
                .map(list -> list.stream()
                        .map(EvaluationResult::fromEntity)
                        .toList());
    }
}
