package com.credit.orchestrator.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para el orchestrator-service.
 *
 * En desarrollo (%dev): CORS habilitado para permitir peticiones desde el
 * frontend de Vite en http://localhost:5173.
 *
 * En producción (%prod): CORS deshabilitado porque nginx actúa como reverse proxy,
 * sirviendo tanto el frontend como el API desde el mismo origen.
 *
 * Configuración definida en application.properties:
 *   %dev.quarkus.http.cors=true
 *   %dev.quarkus.http.cors.origins=http://localhost:5173
 *   %prod.quarkus.http.cors=false
 */
@ConfigMapping(prefix = "cors")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface CorsConfig {
}
