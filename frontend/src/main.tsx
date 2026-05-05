/**
 * Punto de entrada de la aplicación React.
 *
 * Monta el componente App dentro de StrictMode en el elemento #root del DOM.
 * StrictMode habilita verificaciones adicionales en desarrollo.
 */
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
