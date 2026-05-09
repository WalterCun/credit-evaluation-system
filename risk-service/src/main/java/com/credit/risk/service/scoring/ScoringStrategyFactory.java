package com.credit.risk.service.scoring;

/**
 * Factory para crear estrategias de scoring preconfiguradas.
 * 
 * Sigue el patrón Factory Method y permite extensibilidad
 * sin modificar código existente (OCP).
 */
public final class ScoringStrategyFactory {
    
    private ScoringStrategyFactory() {}
    
    public static ScoringStrategy createDebtBurdenStrategy() {
        return new DebtBurdenScoringStrategy();
    }
    
    public static ScoringStrategy createPaymentRatioStrategy() {
        return new PaymentRatioScoringStrategy();
    }
    
    public static ScoringStrategy createDebtLoadStrategy() {
        return new DebtLoadScoringStrategy();
    }
}