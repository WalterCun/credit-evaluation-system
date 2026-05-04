import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createEvaluation, getEvaluations } from "../services/api";
import type { EvaluationRequest } from "../types";

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

export function useEvaluations() {
  return useQuery({
    queryKey: ["evaluations"],
    queryFn: getEvaluations,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}
