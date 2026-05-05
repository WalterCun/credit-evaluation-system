package com.credit.orchestrator.rest;

import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.orchestrator.model.EvaluationResult;
import com.credit.orchestrator.service.CreditEvaluationService;
import com.credit.orchestrator.validator.CreditRequestValidator;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

/**
 * REST Resource - Expone los endpoints HTTP del sistema de evaluación de créditos.
 *
 * Ruta base: /v1/credit-evaluations
 *
 * Endpoints:
 *   POST /v1/credit-evaluations  → Crea una nueva evaluación crediticia.
 *     - Valida la solicitud con CreditRequestValidator.
 *     - Si hay errores de validación, retorna 400 BAD_REQUEST con la lista de errores.
 *     - Si la validación pasa, delega al CreditEvaluationService y retorna 201 CREATED con el resultado.
 *
 *   GET /v1/credit-evaluations   → Lista todas las evaluaciones almacenadas.
 *     - Retorna 200 OK con la lista de EvaluationResult.
 *
 * Ejemplo de request POST:
 *   {
 *     "cedula": "1712345678",
 *     "montoSolicitado": 10000,
 *     "salario": 5000,
 *     "tiempoAnios": 3
 *   }
 *
 * Ejemplo de response POST (201):
 *   {
 *     "id": 1,
 *     "cedula": "1712345678",
 *     "montoSolicitado": 10000.00,
 *     "salario": 5000.00,
 *     "tiempoAnios": 3,
 *     "score": 75,
 *     "deudaMensual": 850.50,
 *     "estado": "APROBADO",
 *     "fechaEvaluacion": "2026-05-05T10:30:00Z"
 *   }
 *
 * Ejemplo de response POST con errores (400):
 *   ["Cédula ecuatoriana inválida (10 dígitos, módulo 10)", "El monto solicitado debe ser positivo"]
 */
@Path("/v1/credit-evaluations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreditEvaluationResource {
    private static final Logger LOG = Logger.getLogger(CreditEvaluationResource.class);

    @Inject
    CreditEvaluationService service;

    /**
     * Crea una nueva evaluación crediticia.
     *
     * Flujo:
     *   1. Valida la request con CreditRequestValidator (cédula, monto, salario, plazo).
     *   2. Si hay errores → retorna 400 con lista de mensajes.
     *   3. Si pasa validación → delega al servicio y retorna 201 con el resultado.
     */
    @POST
    public Uni<Response> create(EvaluationRequest request) {
        LOG.infov("POST /v1/credit-evaluations cedula={0} monto={1} plazo={2}",
                request.getCedula(), request.getMontoSolicitado(), request.getTiempoAnios());

        List<String> errors = CreditRequestValidator.validate(request);
        if (!errors.isEmpty()) {
            LOG.warnv("Validación fallida para cedula={0}. Errores={1}", request.getCedula(), errors);
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errors).build());
        }

        return service.evaluate(request)
                .map(result -> {
                    LOG.infov("Evaluación creada cedula={0} estado={1}", result.getCedula(), result.getEstado());
                    return Response.status(Response.Status.CREATED).entity(result).build();
                });
    }

    /**
     * Lista todas las evaluaciones crediticias almacenadas en PostgreSQL.
     *
     * @return 200 OK con lista de EvaluationResult (puede estar vacía).
     */
    @GET
    public Uni<Response> list() {
        LOG.info("GET /v1/credit-evaluations");
        return service.listAll()
                .map(list -> {
                    LOG.infov("Evaluaciones retornadas: {0}", list.size());
                    return Response.ok(list).build();
                });
    }
}
