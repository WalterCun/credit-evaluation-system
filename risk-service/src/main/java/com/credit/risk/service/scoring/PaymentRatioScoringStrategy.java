package com.credit.risk.service.scoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ScoringStrategy para cálculo de puntos por cuota mensual.
 * 
 * Tabla de puntos:
 *   ratio ≤ 0.15 → 30 pts  (cuota muy cómoda)
 *   ratio ≤ 0.25 → 22 pts
 *   ratio ≤ 0.35 → 14 pts
 *   ratio ≤ 0.40 → 6 pts
 *   ratio > 0.40 → 0 pts   (cuota excesiva)
 */
public class PaymentRatioScoringStrategy implements ScoringStrategy {
    
    private record Threshold(int maxPoints, double ratioMax) {}
    
    private final List<Threshold> thresholds = new ArrayList<>();
    
    public PaymentRatioScoringStrategy() {
        thresholds.add(new Threshold(30, 0.15));
        thresholds.add(new Threshold(22, 0.25));
        thresholds.add(new Threshold(14, 0.35));
        thresholds.add(new Threshold(6, 0.40));
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
        return "PaymentRatioScoringStrategy";
    }
}