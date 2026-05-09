package com.credit.risk.service.scoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ScoringStrategy para cálculo de puntos por endeudamiento.
 * 
 * Tabla de puntos:
 *   ratio ≤ 0.30 → 40 pts  (bajo endeudamiento)
 *   ratio ≤ 0.60 → 30 pts
 *   ratio ≤ 0.80 → 20 pts
 *   ratio ≤ 1.00 → 10 pts
 *   ratio > 1.00 → 0 pts   (sobre-endeudado)
 */
public class DebtBurdenScoringStrategy implements ScoringStrategy {
    
    private record Threshold(int maxPoints, double ratioMax) {}
    
    private final List<Threshold> thresholds = new ArrayList<>();
    
    public DebtBurdenScoringStrategy() {
        thresholds.add(new Threshold(40, 0.30));
        thresholds.add(new Threshold(30, 0.60));
        thresholds.add(new Threshold(20, 0.80));
        thresholds.add(new Threshold(10, 1.00));
    }
    
    @Override
    public int calculatePoints(double ratio) {
        if (ratio < 0) return 0;
        for (Threshold t : thresholds) {
            if (ratio <= t.ratioMax) {
                return t.maxPoints;
            }
        }
        return 0;
    }
    
    @Override
    public String getName() {
        return "DebtBurdenScoringStrategy";
    }
}