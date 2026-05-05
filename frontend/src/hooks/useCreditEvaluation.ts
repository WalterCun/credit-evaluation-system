/**
 * Hooks personalizados para gestión de evaluaciones crediticias con React Query.
 *
 * - useCreditEvaluation: mutation para crear una nueva evaluación.
 *   Al completarse exitosamente, invalida la query de evaluaciones para
 *   que la lista se refresque automáticamente.
 *
 * - useEvaluations: query para obtener el historial de evaluaciones.
 *   Se ejecuta una vez (retry: 1) y no se re-ejecuta al re-ganar foco de ventana.
 */
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createEvaluation, getEvaluations } from "../services/api";
import type { EvaluationRequest } from "../types";

/** Mutation para crear una evaluación crediticia. Invalida la caché de evaluaciones al success. */
export function useCreditEvaluation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: EvaluationRequest) => createEvaluation(data),
    onSuccess: () => {
      console.info("[MUTATION][createEvaluation] success");
      queryClient.invalidateQueries({ queryKey: ["evaluations"] });
    },
    onError: (error) => {
      console.error("[MUTATION][createEvaluation] error", error);
    },
  });
}

/** Query para obtener el listado de evaluaciones crediticias almacenadas en el backend. */
export function useEvaluations() {
  return useQuery({
    queryKey: ["evaluations"],
    queryFn: getEvaluations,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}
