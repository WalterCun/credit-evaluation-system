/**
 * Capa de comunicación con el API REST del orchestrator-service.
 *
 * Configura una instancia de Axios con interceptores para logging
 * y expone funciones para crear evaluaciones y consultar el historial.
 *
 * Base URL: se toma de VITE_API_URL (definida en .env.development para local,
 * o vacía en Docker donde nginx actúa como reverse proxy).
 *
 * Endpoints utilizados:
 *   POST /v1/credit-evaluations  → Crear nueva evaluación
 *   GET  /v1/credit-evaluations  → Listar todas las evaluaciones
 */
import axios from "axios";
import type { EvaluationRequest, EvaluationResult } from "../types";

/** Instancia Axios configurada con base URL y headers por defecto. */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "",
  headers: { "Content-Type": "application/json" },
});

/** Interceptor de solicitud: loguea método, URL y datos de cada petición saliente. */
api.interceptors.request.use((config) => {
  console.info("[API][REQ]", {
    method: config.method?.toUpperCase(),
    url: `${config.baseURL}${config.url}`,
    data: config.data,
  });
  return config;
});

/** Interceptores de respuesta: loguea respuestas exitosas y errores. */
api.interceptors.response.use(
  (response) => {
    console.info("[API][RES]", {
      status: response.status,
      url: `${response.config.baseURL}${response.config.url}`,
      data: response.data,
    });
    return response;
  },
  (error) => {
    console.error("[API][ERR]", {
      message: error.message,
      code: error.code,
      status: error.response?.status,
      url: `${error.config?.baseURL}${error.config?.url}`,
      data: error.response?.data,
    });
    return Promise.reject(error);
  }
);

/**
 * Envía una solicitud de evaluación crediticia al backend.
 *
 * @example
 * const result = await createEvaluation({
 *   cedula: "1712345678",
 *   montoSolicitado: 10000,
 *   salario: 5000,
 *   tiempoAnios: 3
 * });
 * // result.estado → "APROBADO" | "RECHAZADO"
 */
export async function createEvaluation(
  data: EvaluationRequest
): Promise<EvaluationResult> {
  const res = await api.post<EvaluationResult>("/v1/credit-evaluations", data);
  return res.data;
}

/**
 * Obtiene el listado completo de evaluaciones crediticias almacenadas.
 *
 * @example
 * const evaluaciones = await getEvaluations();
 * // evaluaciones[0].score → 75
 */
export async function getEvaluations(): Promise<EvaluationResult[]> {
  const res = await api.get<EvaluationResult[]>("/v1/credit-evaluations");
  return res.data;
}
