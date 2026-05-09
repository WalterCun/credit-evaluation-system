package com.credit.risk.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.credit.risk.grpc.DebtInfo;
import com.credit.risk.service.scoring.ScoringStrategy;
import com.credit.risk.service.scoring.ScoringStrategyFactory;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Servicio de cálculo de riesgo crediticio - Lógica de negocio del risk-service.
 * 
 * REFACTOR SOLID:
 * - SRP: Cada método tiene una única responsabilidad
 * - OCP: Las estrategias de scoring son intercambiables
 * - DIP: Depende de abstracciones (ScoringStrategy) no implementaciones concretas
 */
@ApplicationScoped
public class RiskService {

    /** Entidades financieras ecuatorianas usadas en la simulación de deudas. */
    private static final String[] ENTIDADES = {
        "Banco de Guayaquil", "Banco Pichincha", "Banco Austro",
        "Banco Bolivariano", "Cooperativa JEP", "Cooperativa Alianza"
    };

    /**
     * Genera deudas simuladas para un solicitante.
     *
     * Simula la consulta a un buró de crédito. Genera entre 1 y 5 deudas,
     * cada una con una entidad financiera aleatoria, un monto entre $500-$10,000,
     * y una mensualidad calculada como un porcentaje aleatorio del monto (5%-15%).
     *
     * @param cedula Cédula de identidad del solicitante.
     * @return Lista de DebtInfo con las deudas simuladas.
     */
    public List<DebtInfo> generateDebts(String cedula) {
        var random = ThreadLocalRandom.current();
        int count = 1 + random.nextInt(5);
        List<DebtInfo> debts = new java.util.ArrayList<>();

        for (int i = 0; i < count; i++) {
            double monto = Math.round((500 + random.nextDouble() * 9500) * 100.0) / 100.0;
            double mensualidad = Math.round(monto * (0.05 + random.nextDouble() * 0.10) * 100.0) / 100.0;
            String entidad = ENTIDADES[random.nextInt(ENTIDADES.length)];

            DebtInfo debt = DebtInfo.newBuilder()
                    .setCedula(cedula)
                    .setEntidad(entidad)
                    .setMonto(monto)
                    .setMensualidad(mensualidad)
                    .build();
            debts.add(debt);
        }

        return debts;
    }

    /**
     * Calcula el score de riesgo crediticio para una solicitud.
     *
     * El score (1-100) se determina sumando puntos de 4 factores:
     *   - Puntos por endeudamiento actual vs límite (0-40 pts)
     *   - Puntos por proporción cuota/salario (0-30 pts)
     *   - Puntos por cantidad de deudas activas (0-15 pts)
     *   - Variante aleatoria (1-15 pts) - simula factores no modelados
     *
     * @param cedula         Cédula del solicitante.
     * @param montoSolicitado Monto del crédito solicitado.
     * @param salario        Salario mensual del solicitante.
     * @param tiempoAnios    Plazo del crédito en años.
     * @return Score de riesgo entre 1 y 100. Valores > 70 favorecen la aprobación.
     */
    public int calculateScore(String cedula, double montoSolicitado, double salario, int tiempoAnios) {
        List<DebtInfo> debts = generateDebts(cedula);

        double deudaMensual = debts.stream()
                .mapToDouble(DebtInfo::getMensualidad)
                .sum();

        double cuotaMensual = montoSolicitado / (tiempoAnios * 12.0);
        double limiteEndeudamiento = salario * 0.40;

        int ptsEndeudamiento = calculatePoints(ScoringStrategyFactory.createDebtBurdenStrategy(), 
                deudaMensual / limiteEndeudamiento);
        int ptsCuota = calculatePoints(ScoringStrategyFactory.createPaymentRatioStrategy(), 
                cuotaMensual / salario);
        int ptsCarga = calculatePoints(ScoringStrategyFactory.createDebtLoadStrategy(), 
                (double) debts.size());
        int ptsVariante = 1 + ThreadLocalRandom.current().nextInt(15);

        int score = ptsEndeudamiento + ptsCuota + ptsCarga + ptsVariante;
        return Math.max(1, Math.min(100, score));
    }

    private int calculatePoints(ScoringStrategy strategy, double ratio) {
        if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
            return 0;
        }
        return strategy.calculatePoints(ratio);
    }
}