/**
 * Componente LoadingSpinner - Indicador de carga animado.
 *
 * Se muestra mientras se están obteniendo las evaluaciones del backend.
 * Utiliza una animación CSS de rotación sobre un círculo con borde parcial.
 */
export default function LoadingSpinner() {
  return (
    <div className="card animate-fade-in p-8">
      <div className="flex flex-col items-center justify-center gap-3">
        <div className="relative h-10 w-10">
          <div className="absolute inset-0 rounded-full border-4 border-gray-200" />
          <div className="absolute inset-0 rounded-full border-4 border-transparent border-t-blue-600 animate-spin" />
        </div>
        <p className="text-sm text-gray-500 font-medium">Cargando evaluaciones...</p>
      </div>
    </div>
  );
}
