package com.credit.orchestrator.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;

public class CreditRequestValidator {

    private CreditRequestValidator() {}

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
