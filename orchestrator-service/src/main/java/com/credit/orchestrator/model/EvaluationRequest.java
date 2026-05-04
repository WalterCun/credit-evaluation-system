package com.credit.orchestrator.model;

import java.math.BigDecimal;

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
