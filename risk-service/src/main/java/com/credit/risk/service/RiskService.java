package com.credit.risk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.credit.risk.grpc.DebtInfo;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Servicio de cálculo de riesgo crediticio - Lógica de negocio del risk-service.
 *
 * Este servicio es el núcleo del análisis de riesgo. Proporciona dos funcionalidades:
 *
 * 1. generateDebts(cedula): Genera deudas simuladas para un solicitante.
 *    En producción, esto se conectaría a un buró de crédito real.
 *    La simulación genera entre 1 y 5 deudas con entidades financieras ecuatorianas,
 *    montos entre $500 y $10,000, y mensualidades calculadas como porcentaje del monto.
 *
 * 2. calculateScore(cedula, monto, salario, plazo): Calcula el score de riesgo (1-100).
 *    El score se compone de 4 factores:
 *      a) Puntos por endeudamiento (0-40 pts): ratio deuda_mensual / límite_endeudamiento.
 *      b) Puntos por cuota (0-30 pts): ratio cuota_mensual / salario.
 *      c) Puntos por carga de deudas (0-15 pts): basado en cantidad de deudas activas.
 *      d) Variante aleatoria (1-15 pts): simula factores no modelados.
 *
 *    Score total = ptsEndeudamiento + ptsCuota + ptsCarga + ptsVariante (clamp 1-100).
 *
 * Tabla de puntos por endeudamiento:
 *   ratio ≤ 0.30 → 40 pts  (bajo endeudamiento)
 *   ratio ≤ 0.60 → 30 pts
 *   ratio ≤ 0.80 → 20 pts
 *   ratio ≤ 1.00 → 10 pts
 *   ratio > 1.00 → 0 pts   (sobre-endeudado)
 *
 * Tabla de puntos por cuota:
 *   ratio ≤ 0.15 → 30 pts  (cuota muy cómoda)
 *   ratio ≤ 0.25 → 22 pts
 *   ratio ≤ 0.35 → 14 pts
 *   ratio ≤ 0.40 → 6 pts
 *   ratio > 0.40 → 0 pts   (cuota excesiva)
 *
 * Tabla de puntos por carga de deudas:
 *   0 deudas → 15 pts, 1 → 12, 2 → 9, 3 → 6, 4 → 3, 5+ → 0 pts
 *
 * NOTA: Este es un servicio simulado. En un entorno real, se conectaría a:
 *   - Buró de crédito (Datacrédito, Equifax, etc.)
 *   - Central de riesgos de la Superintendencia de Bancos
 *   - Sistemas de scoring crediticio (FICO-like)
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
     * @param cedula Cédula de identidad del solicitante (usada como seed implícito).
     * @return Lista de DebtInfo con las deudas simuladas.
     */
    public List<DebtInfo> generateDebts(String cedula) {
        var random = ThreadLocalRandom.current();
        int count = 1 + random.nextInt(5);
        List<DebtInfo> debts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            /** Monto de la deuda entre $500 y $10,000 (redondeado a 2 decimales). */
            double monto = Math.round((500 + random.nextDouble() * 9500) * 100.0) / 100.0;
            /** Mensualidad: entre 5% y 15% del monto total de la deuda. */
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
     *   - Endeudamiento actual vs límite (0-40 pts)
     *   - Proporción cuota/salario (0-30 pts)
     *   - Cantidad de deudas activas (0-15 pts)
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

        /** Suma de mensualidades de deudas existentes. */
        double deudaMensual = debts.stream()
                .mapToDouble(DebtInfo::getMensualidad)
                .sum();

        /** Cuota mensual del nuevo crédito solicitado. */
        double cuotaMensual = montoSolicitado / (tiempoAnios * 12.0);

        /** Límite de endeudamiento = 40% del salario. */
        double limiteEndeudamiento = salario * 0.40;

        /** Cálculo de puntos por cada factor de riesgo. */
        int ptsEndeudamiento = calcularPuntosEndeudamiento(deudaMensual, limiteEndeudamiento);
        int ptsCuota = calcularPuntosCuota(cuotaMensual, salario);
        int ptsCarga = calcularPuntosCarga(debts.size());
        /** Variante aleatoria (1-15) que simula factores adicionales no modelados. */
        int ptsVariante = 1 + ThreadLocalRandom.current().nextInt(15);

        int score = ptsEndeudamiento + ptsCuota + ptsCarga + ptsVariante;
        return Math.max(1, Math.min(100, score));
    }

    /**
     * Calcula puntos basados en el ratio de endeudamiento actual.
     *
     * @param deudaMensual Total de deuda mensual existente.
     * @param limite       Límite de endeudamiento permitido (40% del salario).
     * @return Puntos de 0 a 40 según la tabla de rangos.
     */
    private int calcularPuntosEndeudamiento(double deudaMensual, double limite) {
        if (limite <= 0) return 0;
        double ratio = deudaMensual / limite;
        if (ratio <= 0.30) return 40;
        if (ratio <= 0.60) return 30;
        if (ratio <= 0.80) return 20;
        if (ratio <= 1.00) return 10;
        return 0;
    }

    /** Calcula puntos basados en la proporción cuota mensual / salario. */
    private int calcularPuntosCuota(double cuotaMensual, double salario) {
        if (salario <= 0) return 0;
        double ratio = cuotaMensual / salario;
        if (ratio <= 0.15) return 30;
        if (ratio <= 0.25) return 22;
        if (ratio <= 0.35) return 14;
        if (ratio <= 0.40) return 6;
        return 0;
    }

    /** Calcula puntos basados en la cantidad de deudas activas del solicitante. */
    private int calcularPuntosCarga(int numDeudas) {
        if (numDeudas <= 0) return 15;
        if (numDeudas == 1) return 12;
        if (numDeudas == 2) return 9;
        if (numDeudas == 3) return 6;
        if (numDeudas == 4) return 3;
        return 0;
    }
}
