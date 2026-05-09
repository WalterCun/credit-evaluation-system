import { useState, useCallback } from "react";
import { UseFormSetValue, UseFormTrigger } from "react-hook-form";
import { validateCedulaEcuadoriana } from "../utils/cedulaValidator";

export function useCedulaValidation(setValue: UseFormSetValue<any>, trigger: UseFormTrigger<any>) {
  const [liveError, setLiveError] = useState<string | null>(null);

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const raw = e.target.value.replace(/\D/g, "").slice(0, 10);
      setValue("cedula", raw, { shouldValidate: true });

      if (raw.length === 10) {
        const error = validateCedulaEcuadoriana(raw);
        setLiveError(error);
        if (error) {
          trigger("cedula");
        }
      } else {
        setLiveError(null);
      }
    },
    [setValue, trigger]
  );

  const clearError = useCallback(() => setLiveError(null), []);

  return { cedulaLiveError: liveError, handleCedulaChange: handleChange, clearCedulaError: clearError };
}