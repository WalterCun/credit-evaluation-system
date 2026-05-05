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
            <code className="text-xs bg-red-100 px-1.5 py-0.5 rounded font-mono">
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
      <div className="card animate-fade-in p-5 sm:p-7 border-red-200 bg-red-50/80">
        <div className="flex items-start gap-3">
          <svg className="h-5 w-5 text-red-500 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a.75.75 0 000 1.5h.253a.25.25 0 01.244.304l-.459 2.066A1.75 1.75 0 0010.747 15H11a.75.75 0 000-1.5h-.253a.25.25 0 01-.244-.304l.459-2.066A1.75 1.75 0 009.253 9H9z" clipRule="evenodd" />
          </svg>
          <div>
            <p className="text-sm font-semibold text-red-800">Error al cargar evaluaciones</p>
            <p className="text-sm text-red-600 mt-0.5">{getErrorMessage()}</p>
          </div>
        </div>
      </div>
    );

  if (!data || data.length === 0)
    return (
      <div className="card animate-fade-in p-8 text-center">
        <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-gray-100 text-gray-400 mb-3">
          <svg className="h-7 w-7" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" d="M19.5 14.25v-2.625a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 0113.5 7.125v-1.5a3.375 3.375 0 00-3.375-3.375H8.25m0 12.75h8.25m-8.25 3H12M10.5 2.25H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 00-9-9z" />
          </svg>
        </div>
        <p className="text-sm font-medium text-gray-600">No hay evaluaciones registradas</p>
        <p className="text-xs text-gray-400 mt-1">Las evaluaciones aparecerán aquí al crear una nueva</p>
      </div>
    );

  return (
    <div className="card animate-fade-in">
      <div className="px-5 sm:px-7 py-5 border-b border-gray-100">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-indigo-50 text-indigo-600">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z" />
            </svg>
          </div>
          <div>
            <h2 className="text-base sm:text-lg font-bold text-gray-900">
              Historial de Evaluaciones
            </h2>
            <p className="text-xs text-gray-500">
              {data.length} evaluación{data.length !== 1 ? "es" : ""} registrada{data.length !== 1 ? "s" : ""}
            </p>
          </div>
        </div>
      </div>

      <div className="px-5 sm:px-7 py-5">
        {/* Desktop table */}
        <div className="hidden sm:block overflow-x-auto -mx-5 sm:-mx-7">
          <table className="min-w-full">
            <thead>
              <tr className="border-b border-gray-100">
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Cédula
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Monto
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Salario
                </th>
                <th className="px-4 py-3 text-center text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Score
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Deuda Mensual
                </th>
                <th className="px-4 py-3 text-center text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Estado
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Fecha
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {data.map((e: EvaluationResult) => (
                <tr key={e.id} className="hover:bg-blue-50/40 transition-colors duration-150">
                  <td className="px-4 py-3 text-sm font-medium text-gray-900 whitespace-nowrap">
                    {e.cedula}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-700 text-right whitespace-nowrap font-mono">
                    ${e.montoSolicitado.toFixed(2)}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-700 text-right whitespace-nowrap font-mono">
                    ${e.salario.toFixed(2)}
                  </td>
                  <td className="px-4 py-3 text-sm text-center whitespace-nowrap">
                    <span className={`inline-flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold ${
                      e.score > 70 ? "bg-emerald-100 text-emerald-700" : "bg-amber-100 text-amber-700"
                    }`}>
                      {e.score}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-700 text-right whitespace-nowrap font-mono">
                    ${e.deudaMensual?.toFixed(2)}
                  </td>
                  <td className="px-4 py-3 text-sm text-center whitespace-nowrap">
                    <StatusBadge estado={e.estado} />
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-500 text-right whitespace-nowrap">
                    {new Date(e.fechaEvaluacion).toLocaleString("es-EC")}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Mobile cards */}
        <div className="sm:hidden space-y-3">
          {data.map((e: EvaluationResult) => (
            <div key={e.id} className="rounded-xl border border-gray-100 bg-gray-50/50 p-4 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm font-semibold text-gray-900">{e.cedula}</span>
                <StatusBadge estado={e.estado} />
              </div>
              <div className="grid grid-cols-2 gap-y-2 gap-x-4 text-sm">
                <div>
                  <p className="text-xs text-gray-500">Monto</p>
                  <p className="font-mono font-medium text-gray-900">${e.montoSolicitado.toFixed(2)}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500">Salario</p>
                  <p className="font-mono font-medium text-gray-900">${e.salario.toFixed(2)}</p>
                </div>
                <div>
                  <p className="text-xs text-gray-500">Score</p>
                  <span className={`inline-flex h-6 w-6 items-center justify-center rounded-full text-xs font-bold ${
                    e.score > 70 ? "bg-emerald-100 text-emerald-700" : "bg-amber-100 text-amber-700"
                  }`}>
                    {e.score}
                  </span>
                </div>
                <div>
                  <p className="text-xs text-gray-500">Deuda mensual</p>
                  <p className="font-mono font-medium text-gray-900">${e.deudaMensual?.toFixed(2)}</p>
                </div>
              </div>
              <p className="text-xs text-gray-400 pt-1 border-t border-gray-100">
                {new Date(e.fechaEvaluacion).toLocaleString("es-EC")}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
