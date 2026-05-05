package com.credit.orchestrator.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad JPA que mapea la tabla "credit_evaluations" en PostgreSQL.
 *
 * Representa una evaluación crediticia persistida. Los campos corresponden
 * a las columnas definidas en postgres/init.sql:
 *
 *   id              - PK auto-incremental (BIGSERIAL)
 *   cedula          - Cédula ecuatoriana (VARCHAR 10)
 *   monto_solicitado - Monto del crédito en USD (DECIMAL 12,2)
 *   salario         - Salario mensual en USD (DECIMAL 12,2)
 *   tiempo_anios    - Plazo del crédito en años (INTEGER)
 *   score           - Score de riesgo calculado por risk-service (INTEGER, 1-100)
 *   deuda_mensual   - Suma de mensualidades de deudas existentes (DECIMAL 12,2)
 *   estado          - Resultado: "APROBADO" o "RECHAZADO" (VARCHAR 20)
 *   fecha_evaluacion - Timestamp con zona horaria de la evaluación
 *
 * Extiende PanacheEntityBase para operaciones reactivas de persistencia
 * (persistAndFlush, listAll, etc.) con Mutiny.
 */
@Entity
@Table(name = "credit_evaluations")
public class CreditEvaluation extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "cedula", nullable = false, length = 10)
    public String cedula;

    @Column(name = "monto_solicitado", nullable = false, precision = 12, scale = 2)
    public BigDecimal montoSolicitado;

    @Column(name = "salario", nullable = false, precision = 12, scale = 2)
    public BigDecimal salario;

    @Column(name = "tiempo_anios", nullable = false)
    public Integer tiempoAnios;

    @Column(name = "score")
    public Integer score;

    @Column(name = "deuda_mensual", precision = 12, scale = 2)
    public BigDecimal deudaMensual;

    @Column(name = "estado", nullable = false, length = 20)
    public String estado;

    @Column(name = "fecha_evaluacion")
    public OffsetDateTime fechaEvaluacion;

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
}
