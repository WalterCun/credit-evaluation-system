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
 * SRP: Única responsabilidad - manejar peticiones HTTP y delegar al servicio.
 */
@Path("/v1/credit-evaluations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreditEvaluationResource {
    private static final Logger LOG = Logger.getLogger(CreditEvaluationResource.class);

    @Inject
    CreditEvaluationService service;

    @Inject
    CreditRequestValidator validator;

    @POST
    public Uni<Response> create(EvaluationRequest request) {
        LOG.infov("POST /v1/credit-evaluations cedula={0} monto={1} plazo={2}",
                request.getCedula(), request.getMontoSolicitado(), request.getTiempoAnios());

        List<String> errors = validator.validate(request);
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