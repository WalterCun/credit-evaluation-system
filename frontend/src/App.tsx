/**
 * Componente raíz de la aplicación Credit Evaluation System.
 *
 * Estructura de la UI:
 *   - Header sticky con título y descripción.
 *   - CreditForm: formulario para crear nuevas evaluaciones.
 *   - EvaluationList: tabla/historial de evaluaciones previas.
 *   - Footer con copyright dinámico.
 *
 * Usa QueryClientProvider de @tanstack/react-query para gestión
 * de estado asíncrono (caching, invalidación, reintentos).
 */
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import CreditForm from "./components/CreditForm";
import EvaluationList from "./components/EvaluationList";

/** Instancia singleton de QueryClient para toda la aplicación. */
const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="min-h-screen flex flex-col">
        <header className="bg-white/70 backdrop-blur-md border-b border-gray-200/60 sticky top-0 z-30">
          <div className="mx-auto max-w-5xl px-4 sm:px-6 py-4 flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 text-white shadow-sm shadow-blue-600/25">
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.72 0M2.25 6.75a60.07 60.07 0 0115.72 0m-4.97 12a60.1 60.1 0 00-5.76 0m5.76 0V6.75m0 12c.67 0 1.33-.02 2-.05m-2 .05c-.67 0-1.33-.02-2-.05" />
              </svg>
            </div>
            <div>
              <h1 className="text-lg sm:text-xl font-bold text-gray-900 leading-tight">
                Evaluación de Créditos
              </h1>
              <p className="text-xs text-gray-500 hidden sm:block">
                Simulador de elegibilidad crediticia
              </p>
            </div>
          </div>
        </header>

        <main className="flex-1 mx-auto w-full max-w-5xl px-4 sm:px-6 py-6 sm:py-10 space-y-6 sm:space-y-8">
          <CreditForm />
          <EvaluationList />
        </main>

        <footer className="border-t border-gray-200/60 bg-white/40 backdrop-blur-sm">
          <div className="mx-auto max-w-5xl px-4 sm:px-6 py-4 text-center text-xs text-gray-400">
            Sistema de Evaluación de Créditos &copy; {new Date().getFullYear()}
          </div>
        </footer>
      </div>
    </QueryClientProvider>
  );
}
