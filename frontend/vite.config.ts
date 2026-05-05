/**
 * Configuración de Vite para el frontend.
 *
 * - Plugin de React para soporte de JSX/TSX.
 * - Servidor de desarrollo en puerto 5173 con host expuesto (necesario para Docker).
 * - usePolling: true para detectar cambios en sistemas de archivos de Windows (WSL2/Docker).
 * - Proxy: redirige peticiones /v1/* al orchestrator-service (localhost:8080) en desarrollo local.
 *          En Docker, nginx se encarga del proxy reverso, por lo que VITE_API_URL se deja vacío.
 */
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    watch: {
      usePolling: true,
    },
    proxy: {
      '/v1': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
