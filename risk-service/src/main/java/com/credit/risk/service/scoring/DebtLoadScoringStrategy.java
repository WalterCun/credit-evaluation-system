package com.credit.risk.service.scoring;

/**
 * Implementación de ScoringStrategy para cálculo de puntos por cantidad de deudas.
 * 
 * Tabla de puntos:
 *   0 deudas → 15 pts
 *   1 deuda  → 12 pts
 *   2 deudas → 9 pts
 *   3 deudas → 6 pts
 *   4 deudas → 3 pts
 *   5+ deudas → 0 pts
 */
public class DebtLoadScoringStrategy implements ScoringStrategy {
    
    @Override
    public int calculatePoints(double ratio) {
        int numDebts = (int) Math.round(ratio);
        if (numDebts <= 0) return 15;
        if (numDebts == 1) return 12;
        if (numDebts == 2) return 9;
        if (numDebts == 3) return 6;
        if (numDebts == 4) return 3;
        return 0;
    }
    
    @Override
    public String getName() {
        return "DebtLoadScoringStrategy";
    }
}