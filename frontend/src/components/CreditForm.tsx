import { useEffect, useCallback, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useCreditEvaluation } from "../hooks/useCreditEvaluation";
import { validateCedulaEcuadoriana } from "../utils/cedulaValidator";
import {
  VALIDATION_LIMITS,
  formatCurrency,
  sanitizeNumericInput,
  validateNumericRange,
} from "../utils/validationConfig";

const { MONTO, SALARIO, PLAZO } = VALIDATION_LIMITS;

const schema = z.object({
  cedula: z
    .string()
    .min(10, "La cédula debe tener 10 dígitos")
    .max(10, "La cédula debe tener 10 dígitos")
    .regex(/^\d{10}$/, "La cédula debe contener solo dígitos")
    .refine((val) => validateCedulaEcuadoriana(val) === null, {
      message: "Cédula ecuatoriana no válida",
    }),
  montoSolicitado: z.coerce
    .number({ invalid_type_error: "Ingrese un número válido" })
    .int("Solo se permiten números enteros")
    .positive("El monto debe ser positivo")
    .min(MONTO.MIN, `El monto mínimo es $${formatCurrency(MONTO.MIN)}`)
    .max(MONTO.MAX, `El monto máximo es $${formatCurrency(MONTO.MAX)}`),
  salario: z.coerce
    .number({ invalid_type_error: "Ingrese un número válido" })
    .int("Solo se permiten números enteros")
    .positive("El salario debe ser positivo")
    .min(SALARIO.MIN, `El salario mínimo es $${formatCurrency(SALARIO.MIN)}`)
    .max(SALARIO.MAX, `El salario máximo es $${formatCurrency(SALARIO.MAX)}`),
  tiempoAnios: z.coerce
    .number({ invalid_type_error: "Ingrese un número válido" })
    .int("El plazo debe ser un número entero")
    .min(PLAZO.MIN, `El plazo mínimo es ${PLAZO.MIN} año${PLAZO.MIN > 1 ? "s" : ""}`)
    .max(PLAZO.MAX, `El plazo máximo es ${PLAZO.MAX} años`),
});

type FormData = z.infer<typeof schema>;

function FieldError({ message }: { message?: string }) {
  if (!message) return null;
  return (
    <p className="mt-1.5 text-sm text-red-600 flex items-center gap-1 animate-slide-up" role="alert">
      <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
      </svg>
      {message}
    </p>
  );
}

function ValidIcon() {
  return (
    <span className="absolute right-3 top-1/2 -translate-y-1/2 text-green-500">
      <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z" clipRule="evenodd" />
      </svg>
    </span>
  );
}

export default function CreditForm() {
  const mutation = useCreditEvaluation();
  const [cedulaLiveError, setCedulaLiveError] = useState<string | null>(null);
  const [montoDisplay, setMontoDisplay] = useState("");
  const [salarioDisplay, setSalarioDisplay] = useState("");
  const [montoLiveError, setMontoLiveError] = useState<string | null>(null);
  const [salarioLiveError, setSalarioLiveError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    watch,
    setValue,
    setFocus,
    trigger,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    mode: "onChange",
    defaultValues: {
      cedula: "",
      montoSolicitado: undefined as unknown as number,
      salario: undefined as unknown as number,
      tiempoAnios: undefined as unknown as number,
    },
  });

  const cedulaValue = watch("cedula") || "";
  const montoValue = watch("montoSolicitado");
  const salarioValue = watch("salario");
  const tiempoValue = watch("tiempoAnios");

  const cedulaDigits = cedulaValue.replace(/\D/g, "").length;
  const cedulaProgress = Math.min((cedulaDigits / 10) * 100, 100);

  const cedulaError = cedulaLiveError || errors.cedula?.message;
  const montoError = montoLiveError || errors.montoSolicitado?.message;
  const salarioError = salarioLiveError || errors.salario?.message;

  const isCedulaValid = cedulaDigits === 10 && !errors.cedula && !cedulaLiveError;
  const isMontoValid = montoValue !== undefined && montoValue > 0 && !montoError;
  const isSalarioValid = salarioValue !== undefined && salarioValue > 0 && !salarioError;
  const isPlazoValid = tiempoValue !== undefined && tiempoValue > 0 && !errors.tiempoAnios;

  const hasFieldErrors = !!cedulaError || !!montoError || !!salarioError || !!errors.tiempoAnios;
  const hasIncompleteFields = !cedulaValue || montoValue === undefined || salarioValue === undefined || tiempoValue === undefined;

  const handleCedulaChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const raw = e.target.value.replace(/\D/g, "").slice(0, 10);
      setValue("cedula", raw, { shouldValidate: true });

      if (raw.length === 10) {
        const error = validateCedulaEcuadoriana(raw);
        setCedulaLiveError(error);
        if (error) {
          trigger("cedula");
        }
      } else {
        setCedulaLiveError(null);
      }
    },
    [setValue, trigger]
  );

  const handleMontoChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const { display, numeric } = sanitizeNumericInput(e.target.value, MONTO.MAX_DIGITS);
      setMontoDisplay(display);

      if (display.length === 0) {
        setMontoLiveError(null);
        setValue("montoSolicitado", undefined as unknown as number, { shouldValidate: true });
        return;
      }

      const error = validateNumericRange(numeric, MONTO.MIN, MONTO.MAX, MONTO.LABEL);
      setMontoLiveError(error);

      if (error && numeric < MONTO.MIN && display.length < String(MONTO.MIN).length) {
        setValue("montoSolicitado", undefined as unknown as number, { shouldValidate: true });
      } else {
        setValue("montoSolicitado", numeric, { shouldValidate: true });
      }
    },
    [setValue]
  );

  const handleSalarioChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const { display, numeric } = sanitizeNumericInput(e.target.value, SALARIO.MAX_DIGITS);
      setSalarioDisplay(display);

      if (display.length === 0) {
        setSalarioLiveError(null);
        setValue("salario", undefined as unknown as number, { shouldValidate: true });
        return;
      }

      const error = validateNumericRange(numeric, SALARIO.MIN, SALARIO.MAX, SALARIO.LABEL);
      setSalarioLiveError(error);

      if (error && numeric < SALARIO.MIN && display.length < String(SALARIO.MIN).length) {
        setValue("salario", undefined as unknown as number, { shouldValidate: true });
      } else {
        setValue("salario", numeric, { shouldValidate: true });
      }
    },
    [setValue]
  );

  const handleMontoBlur = useCallback(() => {
    const num = montoValue;
    if (num && !isNaN(num) && num > 0) {
      setMontoDisplay(formatCurrency(num));
    }
  }, [montoValue]);

  const handleSalarioBlur = useCallback(() => {
    const num = salarioValue;
    if (num && !isNaN(num) && num > 0) {
      setSalarioDisplay(formatCurrency(num));
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

  const onSubmit = (data: FormData) => {
    mutation.mutate(data, { onSuccess: () => {
      reset();
      setCedulaLiveError(null);
      setMontoLiveError(null);
      setSalarioLiveError(null);
      setMontoDisplay("");
      setSalarioDisplay("");
    }});
  };

  useEffect(() => {
    if (mutation.isError) {
      setFocus("cedula");
    }
}, [mutation.isError, setFocus]);

  const inputClass = (hasError: boolean, isValid2: boolean) =>
    `input-field ${hasError ? "input-field-error" : isValid2 ? "border-green-400 focus:border-green-500 focus:ring-green-500/20" : ""}`;

  return (
    <div className="card animate-fade-in">
      <div className="px-5 sm:px-7 py-5 border-b border-gray-100">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-50 text-blue-600">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m3.75 9v6m3-3H9m1.5-12.75H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
            </svg>
          </div>
          <div>
            <h2 className="text-base sm:text-lg font-bold text-gray-900">
              Nueva Evaluación
            </h2>
            <p className="text-xs text-gray-500">
              Complete los datos del solicitante
            </p>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="px-5 sm:px-7 py-5 space-y-5">
        {/* Cédula */}
        <div>
          <label htmlFor="cedula" className="block text-sm font-medium text-gray-700 mb-1.5">
            Cédula de identidad
          </label>
          <div className="relative">
            <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0" />
              </svg>
            </span>
            <input
              id="cedula"
              type="text"
              inputMode="numeric"
              maxLength={10}
              value={cedulaValue}
              onChange={handleCedulaChange}
              className={`input-field pl-9 pr-10 ${inputClass(!!cedulaError, isCedulaValid)}`}
              placeholder="1712345678"
              aria-invalid={!!cedulaError}
              aria-describedby={cedulaError ? "cedula-error" : "cedula-hint"}
              autoComplete="off"
            />
            {isCedulaValid && <ValidIcon />}
          </div>
          <div className="mt-1.5 flex items-center justify-between">
            {cedulaError ? (
              <p id="cedula-error" className="text-sm text-red-600 flex items-center gap-1" role="alert">
                <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
                </svg>
                {cedulaError}
              </p>
            ) : (
              <p id="cedula-hint" className="text-xs text-gray-400">
                {cedulaDigits < 10
                  ? `${cedulaDigits}/10 dígitos`
                  : "Cédula válida"}
              </p>
            )}
            <div className="h-1 w-20 rounded-full bg-gray-100 overflow-hidden ml-2">
              <div
                className={`h-full rounded-full transition-all duration-300 ${
                  cedulaError ? "bg-red-400" : isCedulaValid ? "bg-green-400" : "bg-blue-400"
                }`}
                style={{ width: `${cedulaProgress}%` }}
              />
            </div>
          </div>
        </div>

        {/* Monto y Salario */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <div>
            <label htmlFor="montoSolicitado" className="block text-sm font-medium text-gray-700 mb-1.5">
              Monto solicitado (USD)
            </label>
            <div className="relative">
              <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm font-medium">
                $
              </span>
        <input
          id="montoSolicitado"
          type="text"
          inputMode="numeric"
          value={montoDisplay}
          onChange={handleMontoChange}
          onBlur={handleMontoBlur}
          onFocus={handleMontoFocus}
          className={`input-field pl-7 pr-10 ${inputClass(!!montoError, isMontoValid)}`}
          placeholder="10000"
          aria-invalid={!!montoError}
          aria-describedby={montoError ? "monto-error" : "monto-hint"}
          autoComplete="off"
        />
        {isMontoValid && <ValidIcon />}
        </div>
        {montoError ? (
          <FieldError message={montoError} />
            ) : (
              <p id="monto-hint" className="mt-1.5 text-xs text-gray-400">
                Rango: ${formatCurrency(MONTO.MIN)} - ${formatCurrency(MONTO.MAX)}
              </p>
            )}
          </div>

          <div>
            <label htmlFor="salario" className="block text-sm font-medium text-gray-700 mb-1.5">
              Salario mensual (USD)
            </label>
            <div className="relative">
              <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm font-medium">
                $
              </span>
        <input
          id="salario"
          type="text"
          inputMode="numeric"
          value={salarioDisplay}
          onChange={handleSalarioChange}
          onBlur={handleSalarioBlur}
          onFocus={handleSalarioFocus}
          className={`input-field pl-7 pr-10 ${inputClass(!!salarioError, isSalarioValid)}`}
          placeholder="2000"
          aria-invalid={!!salarioError}
          aria-describedby={salarioError ? "salario-error" : "salario-hint"}
          autoComplete="off"
        />
        {isSalarioValid && <ValidIcon />}
        </div>
        {salarioError ? (
          <FieldError message={salarioError} />
            ) : (
              <p id="salario-hint" className="mt-1.5 text-xs text-gray-400">
                Rango: ${formatCurrency(SALARIO.MIN)} - ${formatCurrency(SALARIO.MAX)}
              </p>
            )}
          </div>
        </div>

        {/* Plazo */}
        <div>
          <label htmlFor="tiempoAnios" className="block text-sm font-medium text-gray-700 mb-1.5">
            Plazo del crédito (años)
          </label>
          <div className="relative">
            <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </span>
            <input
              id="tiempoAnios"
              type="text"
              inputMode="numeric"
              {...register("tiempoAnios", {
                setValueAs: (v: string) => {
                  const n = parseInt(v, 10);
                  return isNaN(n) ? undefined : n;
                },
              })}
              className={`input-field pl-9 pr-10 ${inputClass(!!errors.tiempoAnios, isPlazoValid)}`}
              placeholder="5"
              aria-invalid={!!errors.tiempoAnios}
              aria-describedby={errors.tiempoAnios ? "plazo-error" : "plazo-hint"}
              autoComplete="off"
            />
            {isPlazoValid && <ValidIcon />}
          </div>
          {errors.tiempoAnios ? (
            <FieldError message={errors.tiempoAnios.message} />
          ) : (
            <p id="plazo-hint" className="mt-1.5 text-xs text-gray-400">
              Rango: {PLAZO.MIN} - {PLAZO.MAX} años
            </p>
          )}
        </div>

        {/* Submit */}
        <button
          type="submit"
          disabled={mutation.isPending || hasFieldErrors || hasIncompleteFields}
          className="btn-primary w-full"
        >
          {mutation.isPending ? (
            <>
              <svg className="h-4 w-4 animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              Evaluando...
            </>
          ) : (
            <>
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12.75L11.25 15 15 9.75M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Evaluar Crédito
            </>
          )}
        </button>

        {(hasFieldErrors || hasIncompleteFields) && (cedulaDigits > 0 || montoDisplay || salarioDisplay || tiempoValue) && (
          <p className="text-xs text-center text-amber-600 flex items-center justify-center gap-1">
            <svg className="h-3.5 w-3.5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M8.485 2.495c.673-1.167 2.357-1.167 3.03 0l6.28 10.875c.673 1.167-.168 2.625-1.516 2.625H3.72c-1.347 0-2.189-1.458-1.515-2.625L8.485 2.495zM10 6a.75.75 0 01.75.75v3.5a.75.75 0 01-1.5 0v-3.5A.75.75 0 0110 6zm0 9a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
            </svg>
            {hasFieldErrors ? "Corrija los errores en los campos para habilitar la evaluación" : "Complete todos los campos correctamente para habilitar la evaluación"}
          </p>
        )}

        {/* Mutation error */}
        {mutation.isError && (
          <div className="animate-slide-up rounded-lg bg-red-50 border border-red-200 p-4" role="alert">
            <div className="flex items-start gap-3">
              <svg className="h-5 w-5 text-red-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.28 7.22a.75.75 0 00-1.06 1.06L8.94 10l-1.72 1.72a.75.75 0 101.06 1.06L10 11.06l1.72 1.72a.75.75 0 101.06-1.06L11.06 10l1.72-1.72a.75.75 0 00-1.06-1.06L10 8.94 8.28 7.22z" clipRule="evenodd" />
              </svg>
              <div>
                <p className="text-sm font-semibold text-red-800">Error al evaluar</p>
                <p className="text-sm text-red-600 mt-0.5">Verifique los datos e intente nuevamente.</p>
              </div>
            </div>
          </div>
        )}

        {/* Mutation success */}
        {mutation.isSuccess && (
          <div
            className={`animate-slide-up rounded-lg border p-4 ${
              mutation.data.estado === "APROBADO"
                ? "bg-emerald-50 border-emerald-200"
                : "bg-red-50 border-red-200"
            }`}
          >
            <div className="flex items-start gap-3">
              {mutation.data.estado === "APROBADO" ? (
                <svg className="h-5 w-5 text-emerald-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z" clipRule="evenodd" />
                </svg>
              ) : (
                <svg className="h-5 w-5 text-red-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.28 7.22a.75.75 0 00-1.06 1.06L8.94 10l-1.72 1.72a.75.75 0 101.06 1.06L10 11.06l1.72 1.72a.75.75 0 101.06-1.06L11.06 10l1.72-1.72a.75.75 0 00-1.06-1.06L10 8.94 8.28 7.22z" clipRule="evenodd" />
                </svg>
              )}
              <div className="flex-1">
                <p className={`text-sm font-semibold ${
                  mutation.data.estado === "APROBADO" ? "text-emerald-800" : "text-red-800"
                }`}>
                  Resultado: {mutation.data.estado}
                </p>
                <div className="mt-1.5 grid grid-cols-2 gap-x-4 gap-y-1 text-sm">
                  <div>
                    <span className="text-gray-500">Score:</span>{" "}
                    <span className="font-semibold text-gray-900">{mutation.data.score}</span>
                  </div>
                  <div>
                    <span className="text-gray-500">Deuda mensual:</span>{" "}
                    <span className="font-semibold text-gray-900">
                      ${mutation.data.deudaMensual?.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </form>
    </div>
  );
}
