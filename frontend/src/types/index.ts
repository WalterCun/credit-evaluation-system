export interface EvaluationRequest {
  cedula: string;
  montoSolicitado: number;
  salario: number;
  tiempoAnios: number;
}

export interface EvaluationResult {
  id: number;
  cedula: string;
  montoSolicitado: number;
  salario: number;
  tiempoAnios: number;
  score: number;
  deudaMensual: number;
  estado: "APROBADO" | "RECHAZADO";
  fechaEvaluacion: string;
}