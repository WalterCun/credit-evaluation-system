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

/**
 * Servicio principal de evaluación crediticia - Orquesta la lógica de negocio.
 *
 * Flujo de evaluate():
 *   1. Llama en paralelo al risk-service vía gRPC para obtener:
 *      a) Score de riesgo (GetScore) → entero 1-100
 *      b) Deudas existentes (GetDebts) → lista de DebtInfo con mensualidades
 *   2. Calcula la deuda mensual total sumando las mensualidades de todas las deudas.
 *   3. Calcula la cuota mensual del nuevo crédito = montoSolicitado / (tiempoAnios * 12).
 *   4. Calcula el total mensual = deudaMensual + cuotaMensual.
 *   5. Calcula el límite de endeudamiento = salario * 40% (ratio de endeudamiento permitido).
 *   6. Determina el estado:
 *      - "APROBADO" si score > 70 Y totalMensual < límiteEndeudamiento
 *      - "RECHAZADO" en caso contrario
 *   7. Persiste la evaluación en PostgreSQL y retorna el resultado como EvaluationResult.
 *
 * Ejemplo de cálculo:
 *   cedula=1712345678, monto=10000, salario=5000, plazo=3
 *   → score=75, deudaMensual=850.50, cuotaMensual=277.78
 *   → totalMensual=1128.28, limite=2000.00
 *   → 75>70 && 1128.28<2000.00 → APROBADO
 */
@ApplicationScoped
public class CreditEvaluationService {
    private static final Logger LOG = Logger.getLogger(CreditEvaluationService.class);

    @Inject
    RiskClient riskClient;

    /**
     * Ejecuta la evaluación crediticia completa para una solicitud.
     *
     * Usa Uni.combine().all().unis() para ejecutar ambas llamadas gRPC en paralelo
     * y esperar a que ambas completen antes de procesar los resultados.
     *
     * @param request Datos de la solicitud (cédula, monto, salario, plazo).
     * @return Uni con el EvaluationResult conteniendo score, deuda, estado, etc.
     */
    @WithTransaction
    public Uni<EvaluationResult> evaluate(EvaluationRequest request) {
        LOG.debugv("Iniciando evaluación para cedula={0}", request.getCedula());

        /** Llamadas gRPC en paralelo al risk-service. */
        Uni<RiskScoreResponse> scoreUni = riskClient.getScore(request);
        Uni<List<DebtInfo>> debtsUni = riskClient.getDebts(request.getCedula());

        return Uni.combine().all().unis(scoreUni, debtsUni).asTuple()
                .flatMap(tuple -> {
                    int score = tuple.getItem1().getScore();
                    List<DebtInfo> debts = tuple.getItem2();

                    /** Suma de mensualidades de deudas existentes del solicitante. */
                    BigDecimal deudaMensual = debts.stream()
                            .map(d -> BigDecimal.valueOf(d.getMensualidad()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    /** Cuota mensual del nuevo crédito = monto / (años * 12 meses). */
                    BigDecimal cuotaMensual = request.getMontoSolicitado()
                            .divide(BigDecimal.valueOf(request.getTiempoAnios() * 12L), 2, RoundingMode.HALF_UP);

                    /** Total mensual = deuda existente + cuota del nuevo crédito. */
                    BigDecimal totalMensual = deudaMensual.add(cuotaMensual);

                    /** Límite de endeudamiento = 40% del salario mensual. */
                    BigDecimal limiteEndeudamiento = request.getSalario().multiply(BigDecimal.valueOf(0.40));

                    /**
                     * Decisión de aprobación:
                     *   - Score > 70 (buena calificación crediticia)
                     *   - Total mensual < 40% del salario (capacidad de pago suficiente)
                     */
                    String estado = (score > 70 && totalMensual.compareTo(limiteEndeudamiento) < 0)
                            ? "APROBADO" : "RECHAZADO";

                    LOG.infov("Resultado preliminar cedula={0} score={1} deudaMensual={2} cuotaNueva={3} estado={4}",
                            request.getCedula(), score, deudaMensual, cuotaMensual, estado);

                    /** Construye la entidad y la persiste en PostgreSQL. */
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

    /**
     * Retorna el historial completo de evaluaciones crediticias almacenadas.
     *
     * @return Uni con lista de EvaluationResult ordenada por fecha de inserción.
     */
    @WithTransaction
    public Uni<List<EvaluationResult>> listAll() {
        LOG.debug("Consultando historial de evaluaciones");
        return CreditEvaluation.<CreditEvaluation>listAll()
                .map(list -> list.stream()
                        .map(EvaluationResult::fromEntity)
                        .toList());
    }
}
