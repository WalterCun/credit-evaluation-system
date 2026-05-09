import { useState, useCallback } from "react";
import { UseFormSetValue, UseFormWatch } from "react-hook-form";
import { VALIDATION_LIMITS, formatCurrency, sanitizeNumericInput, validateNumericRange } from "../utils/validationConfig";

export function useNumericInput(setValue: UseFormSetValue<any>, watch: UseFormWatch<any>) {
  const { MONTO, SALARIO } = VALIDATION_LIMITS;

  const montoValue = watch("montoSolicitado");
  const salarioValue = watch("salario");

  const [montoDisplay, setMontoDisplay] = useState("");
  const [salarioDisplay, setSalarioDisplay] = useState("");
  const [montoLiveError, setMontoLiveError] = useState<string | null>(null);
  const [salarioLiveError, setSalarioLiveError] = useState<string | null>(null);

  const handleMontoChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { display, numeric } = sanitizeNumericInput(e.target.value, MONTO.MAX_DIGITS);
    setMontoDisplay(display);

    if (display.length === 0) {
      setMontoLiveError(null);
      setValue("montoSolicitado", undefined as unknown as number, { shouldValidate: true });
      return;
    }

    const error = validateNumericRange(numeric, MONTO.MIN, MONTO.MAX, MONTO.LABEL);
    setMontoLiveError(error);
    setValue("montoSolicitado", numeric, { shouldValidate: true });
  }, [setValue]);

  const handleSalarioChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { display, numeric } = sanitizeNumericInput(e.target.value, SALARIO.MAX_DIGITS);
    setSalarioDisplay(display);

    if (display.length === 0) {
      setSalarioLiveError(null);
      setValue("salario", undefined as unknown as number, { shouldValidate: true });
      return;
    }

    const error = validateNumericRange(numeric, SALARIO.MIN, SALARIO.MAX, SALARIO.LABEL);
    setSalarioLiveError(error);
    setValue("salario", numeric, { shouldValidate: true });
  }, [setValue]);

  const handleMontoBlur = useCallback(() => {
    if (montoValue && !isNaN(montoValue) && montoValue > 0) {
      setMontoDisplay(formatCurrency(montoValue));
    }
  }, [montoValue]);

  const handleSalarioBlur = useCallback(() => {
    if (salarioValue && !isNaN(salarioValue) && salarioValue > 0) {
      setSalarioDisplay(formatCurrency(salarioValue));
    }
  }, [salarioValue]);

  const handleMontoFocus = useCallback(() => {
    if (montoValue !== undefined && montoValue > 0) {
      setMontoDisplay(String(Math.round(montoValue)));
    }
  }, [montoValue]);

  const handleSalarioFocus = useCallback(() => {
    if (salarioValue !== undefined && salarioValue > 0) {
      setSalarioDisplay(String(Math.round(salarioValue)));
    }
  }, [salarioValue]);

  const clearErrors = useCallback(() => {
    setMontoLiveError(null);
    setSalarioLiveError(null);
    setMontoDisplay("");
    setSalarioDisplay("");
  }, []);

  return {
    montoDisplay,
    montoError: montoLiveError,
    handleMontoChange,
    handleMontoBlur,
    handleMontoFocus,
    salarioDisplay,
    salarioError: salarioLiveError,
    handleSalarioChange,
    handleSalarioBlur,
    handleSalarioFocus,
    clearErrors,
  };
}