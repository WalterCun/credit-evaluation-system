package com.credit.orchestrator.model;

import java.math.BigDecimal;

/**
 * DTO de entrada para crear una evaluación crediticia.
 *
 * Se deserializa del JSON del body del POST /v1/credit-evaluations.
 * Los campos son validados por CreditRequestValidator antes de procesar.
 *
 * Ejemplo JSON:
 *   {
 *     "cedula": "1712345678",
 *     "montoSolicitado": 10000,
 *     "salario": 5000,
 *     "tiempoAnios": 3
 *   }
 */
public class EvaluationRequest {
    private String cedula;
    private BigDecimal montoSolicitado;
    private BigDecimal salario;
    private Integer tiempoAnios;

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public Integer getTiempoAnios() { return tiempoAnios; }
    public void setTiempoAnios(Integer tiempoAnios) { this.tiempoAnios = tiempoAnios; }
}
