package com.credit.orchestrator.service;

import java.math.BigDecimal;

/**
 * Calculador de decisiones de evaluación crediticia.
 * 
 * SRP: Única responsabilidad - determinar si una solicitud es aprobada o rechazada.
 * OCP: Extensión mediante parámetros configurables.
 */
public final class DecisionCalculator {
    
    private static final int SCORE_THRESHOLD = 70;
    private static final double DEBT_TO_INCOME_RATIO = 0.40;
    
    private DecisionCalculator() {}
    
    /**
     * Determina el estado de la evaluación según score y capacidad de pago.
     * 
     * @param score Score de riesgo (1-100)
     * @param totalMonthlyDebt Total de deuda mensual + nueva cuota
     * @param monthlyIncome Salario mensual
     * @return "APROBADO" o "RECHAZADO"
     */
    public static String calculateState(int score, BigDecimal totalMonthlyDebt, BigDecimal monthlyIncome) {
        boolean scoreApproved = score > SCORE_THRESHOLD;
        boolean debtWithinLimit = totalMonthlyDebt.compareTo(monthlyIncome.multiply(BigDecimal.valueOf(DEBT_TO_INCOME_RATIO))) < 0;
        return scoreApproved && debtWithinLimit ? "APROBADO" : "RECHAZADO";
    }
}