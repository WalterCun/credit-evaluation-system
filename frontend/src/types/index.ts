/**
 * Tipos compartidos del frontend para el sistema de evaluación de créditos.
 *
 * Estos tipos definen la estructura de datos que se envía al backend
 * (EvaluationRequest) y la que se recibe como respuesta (EvaluationResult).
 * Deben mantenerse sincronizados con los modelos del orchestrator-service.
 */

/** Datos que el usuario ingresa en el formulario para solicitar una evaluación crediticia. */
export interface EvaluationRequest {
  /** Cédula de identidad ecuatoriana (10 dígitos numéricos). */
  cedula: string;
  /** Monto del crédito solicitado en USD (entero, 500 - 500,000). */
  montoSolicitado: number;
  /** Salario mensual del solicitante en USD (entero, 100 - 100,000). */
  salario: number;
  /** Plazo del crédito en años (1 - 30). */
  tiempoAnios: number;
}

/** Resultado de la evaluación crediticia devuelto por el backend. */
export interface EvaluationResult {
  /** Identificador único de la evaluación (generado por PostgreSQL). */
  id: number;
  /** Cédula del solicitante. */
  cedula: string;
  /** Monto solicitado en USD. */
  montoSolicitado: number;
  /** Salario mensual en USD. */
  salario: number;
  /** Plazo en años. */
  tiempoAnios: number;
  /** Score de riesgo calculado por risk-service (1 - 100). Valores > 70 favorecen la aprobación. */
  score: number;
  /** Total de deuda mensual del solicitante (suma de mensualidades de deudas existentes). */
  deudaMensual: number;
  /** Estado final de la evaluación: "APROBADO" si score > 70 y deuda < 40% del salario, sino "RECHAZADO". */
  estado: "APROBADO" | "RECHAZADO";
  /** Fecha y hora en que se realizó la evaluación (ISO 8601). */
  fechaEvaluacion: string;
}
