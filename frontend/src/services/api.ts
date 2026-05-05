import axios from "axios";
import type { EvaluationRequest, EvaluationResult } from "../types";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  console.info("[API][REQ]", {
    method: config.method?.toUpperCase(),
    url: `${config.baseURL}${config.url}`,
    data: config.data,
  });
  return config;
});

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

export async function createEvaluation(
  data: EvaluationRequest
): Promise<EvaluationResult> {
  const res = await api.post<EvaluationResult>("/v1/credit-evaluations", data);
  return res.data;
}

export async function getEvaluations(): Promise<EvaluationResult[]> {
  const res = await api.get<EvaluationResult[]>("/v1/credit-evaluations");
  return res.data;
}
