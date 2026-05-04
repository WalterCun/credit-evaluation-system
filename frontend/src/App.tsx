import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import CreditForm from "./components/CreditForm";
import EvaluationList from "./components/EvaluationList";

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="mx-auto max-w-4xl px-4 py-8">
        <h1 className="mb-8 text-3xl font-bold text-gray-900">
          Sistema de Evaluación de Créditos
        </h1>
        <div className="space-y-8">
          <CreditForm />
          <EvaluationList />
        </div>
      </div>
    </QueryClientProvider>
  );
}
