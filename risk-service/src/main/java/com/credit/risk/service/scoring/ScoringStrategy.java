package com.credit.risk.service.scoring;

/**
 * Estrategia para cálculo de puntos de un factor de riesgo.
 * 
 * Implementa OCP permitiendo agregar nuevas estrategias sin modificar código existente.
 * Cada estrategia encapsula una tabla de puntuación específica.
 */
public interface ScoringStrategy {
    /**
     * Calcula los puntos según el ratio proporcionado.
     * 
     * @param ratio Valor normalizado entre 0 y 1 (o superior)
     * @return Puntos asignados según la estrategia
     */
    int calculatePoints(double ratio);
    
    /**
     * Nombre identificador de la estrategia (para logging/debug).
     */
    String getName();
}