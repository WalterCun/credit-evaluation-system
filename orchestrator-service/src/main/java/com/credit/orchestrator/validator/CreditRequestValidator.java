package com.credit.orchestrator.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;

/**
 * Validador de solicitudes de evaluación crediticia.
 *
 * Aplica reglas de negocio antes de procesar una evaluación:
 *   1. Cédula: debe ser una cédula ecuatoriana válida (10 dígitos, Módulo 10).
 *   2. Monto solicitado: debe ser positivo (> 0).
 *   3. Salario: debe ser positivo (> 0).
 *   4. Tiempo en años: debe estar entre 1 y 30 años.
 *
 * Retorna una lista de mensajes de error. Si la lista está vacía, la solicitud es válida.
 *
 * Ejemplo de errores retornados:
 *   ["Cédula ecuatoriana inválida (10 dígitos, módulo 10)", "El monto solicitado debe ser positivo"]
 */
public class CreditRequestValidator {

    private CreditRequestValidator() {}

    /**
     * Valida todos los campos de una EvaluationRequest.
     *
     * @param request Solicitud a validar.
     * @return Lista de mensajes de error. Vacía si la solicitud es válida.
     */
    public static List<String> validate(EvaluationRequest request) {
        List<String> errors = new ArrayList<>();
        if (request.getCedula() == null || !CedulaValidator.isValid(request.getCedula())) {
            errors.add("Cédula ecuatoriana inválida (10 dígitos, módulo 10)");
        }
        if (request.getMontoSolicitado() == null || request.getMontoSolicitado().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("El monto solicitado debe ser positivo");
        }
        if (request.getSalario() == null || request.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("El salario debe ser positivo");
        }
        if (request.getTiempoAnios() == null || request.getTiempoAnios() < 1 || request.getTiempoAnios() > 30) {
            errors.add("El tiempo debe estar entre 1 y 30 años");
        }
        return errors;
    }
}
