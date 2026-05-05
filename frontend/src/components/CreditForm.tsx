import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useCreditEvaluation } from "../hooks/useCreditEvaluation";

const schema = z.object({
  cedula: z
    .string()
    .min(10, "La cédula debe tener 10 dígitos")
    .max(10, "La cédula debe tener 10 dígitos")
    .regex(/^\d{10}$/, "La cédula debe contener solo dígitos"),
  montoSolicitado: z.coerce.number().positive("El monto debe ser positivo"),
  salario: z.coerce.number().positive("El salario debe ser positivo"),
  tiempoAnios: z.coerce
    .number()
    .int()
    .min(1, "Mínimo 1 año")
    .max(30, "Máximo 30 años"),
});

type FormData = z.infer<typeof schema>;

export default function CreditForm() {
  const mutation = useCreditEvaluation();
  const {
    register,
    handleSubmit,
    reset,
    watch,
    setFocus,
    formState: { errors, touchedFields, isValid },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    mode: "onTouched",
  });

  const [cedulaValue] = [watch("cedula") || ""];

  const onSubmit = (data: FormData) => {
    mutation.mutate(data, { onSuccess: () => reset() });
  };

  useEffect(() => {
    if (mutation.isError) {
      setFocus("cedula");
    }
  }, [mutation.isError, setFocus]);

  const cedulaDigits = cedulaValue.replace(/\D/g, "").length;
  const cedulaProgress = Math.min((cedulaDigits / 10) * 100, 100);

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
              {...register("cedula")}
              className={`input-field pl-9 ${errors.cedula ? "input-field-error" : ""} ${
                touchedFields.cedula && !errors.cedula && cedulaDigits === 10 ? "border-green-400 focus:border-green-500 focus:ring-green-500/20" : ""
              }`}
              placeholder="1712345678"
              aria-invalid={errors.cedula ? "true" : "false"}
              aria-describedby={errors.cedula ? "cedula-error" : undefined}
            />
            {touchedFields.cedula && !errors.cedula && cedulaDigits === 10 && (
              <span className="absolute right-3 top-1/2 -translate-y-1/2 text-green-500">
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.857-9.809a.75.75 0 00-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 10-1.06 1.061l2.5 2.5a.75.75 0 001.137-.089l4-5.5z" clipRule="evenodd" />
                </svg>
              </span>
            )}
          </div>
          <div className="mt-1.5 flex items-center justify-between">
            {errors.cedula ? (
              <p id="cedula-error" className="text-sm text-red-600 flex items-center gap-1" role="alert">
                <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
                </svg>
                {errors.cedula.message}
              </p>
            ) : (
              <p className="text-xs text-gray-400">
                {cedulaDigits}/10 dígitos
              </p>
            )}
            <div className="h-1 w-20 rounded-full bg-gray-100 overflow-hidden ml-2">
              <div
                className={`h-full rounded-full transition-all duration-300 ${
                  errors.cedula ? "bg-red-400" : cedulaDigits === 10 ? "bg-green-400" : "bg-blue-400"
                }`}
                style={{ width: `${cedulaProgress}%` }}
              />
            </div>
          </div>
        </div>

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
                type="number"
                step="0.01"
                min="0"
                {...register("montoSolicitado")}
                className={`input-field pl-7 ${errors.montoSolicitado ? "input-field-error" : ""}`}
                placeholder="10,000.00"
                aria-invalid={errors.montoSolicitado ? "true" : "false"}
                aria-describedby={errors.montoSolicitado ? "monto-error" : undefined}
              />
            </div>
            {errors.montoSolicitado && (
              <p id="monto-error" className="mt-1.5 text-sm text-red-600 flex items-center gap-1" role="alert">
                <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
                </svg>
                {errors.montoSolicitado.message}
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
                type="number"
                step="0.01"
                min="0"
                {...register("salario")}
                className={`input-field pl-7 ${errors.salario ? "input-field-error" : ""}`}
                placeholder="2,000.00"
                aria-invalid={errors.salario ? "true" : "false"}
                aria-describedby={errors.salario ? "salario-error" : undefined}
              />
            </div>
            {errors.salario && (
              <p id="salario-error" className="mt-1.5 text-sm text-red-600 flex items-center gap-1" role="alert">
                <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
                </svg>
                {errors.salario.message}
              </p>
            )}
          </div>
        </div>

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
              type="number"
              min="1"
              max="30"
              {...register("tiempoAnios")}
              className={`input-field pl-9 ${errors.tiempoAnios ? "input-field-error" : ""}`}
              placeholder="5"
              aria-invalid={errors.tiempoAnios ? "true" : "false"}
              aria-describedby={errors.tiempoAnios ? "plazo-error" : undefined}
            />
          </div>
          {errors.tiempoAnios && (
            <p id="plazo-error" className="mt-1.5 text-sm text-red-600 flex items-center gap-1" role="alert">
              <svg className="h-4 w-4 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-8-5a.75.75 0 01.75.75v4.5a.75.75 0 01-1.5 0v-4.5A.75.75 0 0110 5zm0 10a1 1 0 100-2 1 1 0 000 2z" clipRule="evenodd" />
              </svg>
              {errors.tiempoAnios.message}
            </p>
          )}
        </div>

        <button
          type="submit"
          disabled={mutation.isPending || !isValid}
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
