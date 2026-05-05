package com.credit.orchestrator.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de salida con el resultado completo de una evaluación crediticia.
 *
 * Se serializa como JSON en las respuestas del API REST.
 * Incluye todos los datos de la solicitud más los resultados del análisis:
 * score, deuda mensual, estado y fecha de evaluación.
 *
 * Ejemplo JSON de respuesta:
 *   {
 *     "id": 1,
 *     "cedula": "1712345678",
 *     "montoSolicitado": 10000.00,
 *     "salario": 5000.00,
 *     "tiempoAnios": 3,
 *     "score": 75,
 *     "deudaMensual": 850.50,
 *     "estado": "APROBADO",
 *     "fechaEvaluacion": "2026-05-05T10:30:00+00:00"
 *   }
 */
public class EvaluationResult {
    private Long id;
    private String cedula;
    private BigDecimal montoSolicitado;
    private BigDecimal salario;
    private Integer tiempoAnios;
    private Integer score;
    private BigDecimal deudaMensual;
    private String estado;
    private OffsetDateTime fechaEvaluacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public Integer getTiempoAnios() { return tiempoAnios; }
    public void setTiempoAnios(Integer tiempoAnios) { this.tiempoAnios = tiempoAnios; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public BigDecimal getDeudaMensual() { return deudaMensual; }
    public void setDeudaMensual(BigDecimal deudaMensual) { this.deudaMensual = deudaMensual; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(OffsetDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    /**
     * Factory method que convierte la entidad JPA CreditEvaluation en el DTO EvaluationResult.
     * Separa la capa de persistencia de la capa de presentación REST.
     */
    public static EvaluationResult fromEntity(CreditEvaluation e) {
        EvaluationResult r = new EvaluationResult();
        r.setId(e.getId());
        r.setCedula(e.getCedula());
        r.setMontoSolicitado(e.getMontoSolicitado());
        r.setSalario(e.getSalario());
        r.setTiempoAnios(e.getTiempoAnios());
        r.setScore(e.getScore());
        r.setDeudaMensual(e.getDeudaMensual());
        r.setEstado(e.getEstado());
        r.setFechaEvaluacion(e.getFechaEvaluacion());
        return r;
    }
}
