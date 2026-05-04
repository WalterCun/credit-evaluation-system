import { useEvaluations } from "../hooks/useCreditEvaluation";
import StatusBadge from "./StatusBadge";
import LoadingSpinner from "./LoadingSpinner";
import type { EvaluationResult } from "../types";
import { isAxiosError } from "axios";

export default function EvaluationList() {
  const { data, isLoading, isError, error } = useEvaluations();

  const getErrorMessage = () => {
    if (isAxiosError(error)) {
      if (error.code === "ERR_NETWORK" || !error.response) {
        return (
          <>
            No se pudo conectar al servidor. Verifica que el API esté corriendo en{" "}
            <code className="text-sm bg-red-50 px-1 rounded">
              {error.config?.baseURL}
            </code>
          </>
        );
      }
      const status = error.response?.status;
      const detail = error.response?.data;
      if (status === 404) return "El endpoint no fue encontrado.";
      if (status === 500)
        return `Error interno del servidor: ${JSON.stringify(detail)}`;
      return `Error ${status}: ${JSON.stringify(detail)}`;
    }
    return error?.message ?? "Error desconocido.";
  };

  if (isLoading) return <LoadingSpinner />;
  if (isError)
    return (
      <div className="rounded-xl bg-red-50 p-6 border border-red-200">
        <p className="text-red-700 font-semibold mb-1">
          Error al cargar evaluaciones
        </p>
        <p className="text-red-600 text-sm">{getErrorMessage()}</p>
      </div>
    );
  if (!data || data.length === 0)
    return <p className="text-gray-500">No hay evaluaciones registradas.</p>;

  return (
    <div className="overflow-x-auto rounded-xl bg-white shadow">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Cédula
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Monto
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Salario
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Score
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Deuda Mensual
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Estado
            </th>
            <th className="px-4 py-3 text-left text-xs font-semibold uppercase text-gray-500">
              Fecha
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {data.map((e: EvaluationResult) => (
            <tr key={e.id} className="hover:bg-gray-50">
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                {e.cedula}
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                ${e.montoSolicitado.toFixed(2)}
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                ${e.salario.toFixed(2)}
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                {e.score}
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                ${e.deudaMensual?.toFixed(2)}
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm">
                <StatusBadge estado={e.estado} />
              </td>
              <td className="whitespace-nowrap px-4 py-3 text-sm text-gray-500">
                {new Date(e.fechaEvaluacion).toLocaleString("es-EC")}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
