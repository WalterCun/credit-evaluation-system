import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useCreditEvaluation } from "../hooks/useCreditEvaluation";

const schema = z.object({
  cedula: z
    .string()
    .length(10, "La cédula debe tener 10 dígitos")
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
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = (data: FormData) => {
    mutation.mutate(data, { onSuccess: () => reset() });
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="space-y-4 rounded-xl bg-white p-6 shadow"
    >
      <h2 className="text-xl font-bold text-gray-800">
        Nueva Evaluación de Crédito
      </h2>

      <div>
        <label className="block text-sm font-medium text-gray-700">
          Cédula
        </label>
        <input
          {...register("cedula")}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-blue-500"
          placeholder="1712345678"
        />
        {errors.cedula && (
          <p className="mt-1 text-sm text-red-600">{errors.cedula.message}</p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">
          Monto Solicitado ($)
        </label>
        <input
          type="number"
          step="0.01"
          {...register("montoSolicitado")}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-blue-500"
          placeholder="10000"
        />
        {errors.montoSolicitado && (
          <p className="mt-1 text-sm text-red-600">
            {errors.montoSolicitado.message}
          </p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">
          Salario ($)
        </label>
        <input
          type="number"
          step="0.01"
          {...register("salario")}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-blue-500"
          placeholder="2000"
        />
        {errors.salario && (
          <p className="mt-1 text-sm text-red-600">{errors.salario.message}</p>
        )}
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700">
          Tiempo (años)
        </label>
        <input
          type="number"
          {...register("tiempoAnios")}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 focus:border-blue-500 focus:ring-blue-500"
          placeholder="5"
        />
        {errors.tiempoAnios && (
          <p className="mt-1 text-sm text-red-600">
            {errors.tiempoAnios.message}
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full rounded-md bg-blue-600 px-4 py-2 text-white font-semibold hover:bg-blue-700 disabled:opacity-50"
      >
        {mutation.isPending ? "Evaluando..." : "Evaluar Crédito"}
      </button>

      {mutation.isError && (
        <p className="text-sm text-red-600">
          Error al evaluar. Verifique los datos e intente nuevamente.
        </p>
      )}

      {mutation.isSuccess && (
        <div
          className={`rounded-md p-4 ${
            mutation.data.estado === "APROBADO"
              ? "bg-green-50 text-green-800"
              : "bg-red-50 text-red-800"
          }`}
        >
          <p className="font-semibold">
            Resultado: {mutation.data.estado}
          </p>
          <p className="text-sm">
            Score: {mutation.data.score} | Deuda mensual: $
            {mutation.data.deudaMensual?.toFixed(2)}
          </p>
        </div>
      )}
    </form>
  );
}
