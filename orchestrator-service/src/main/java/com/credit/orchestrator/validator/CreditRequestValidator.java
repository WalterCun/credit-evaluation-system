package com.credit.orchestrator.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.credit.orchestrator.model.EvaluationRequest;

/**
 * Validador de solicitudes de evaluación crediticia.
 * 
 * SRP: Coordina validaciones de campos individuales.
 * OCP: Extiende funcionalidad agregando nuevos validadores.
 */
public class CreditRequestValidator {

    private final CedulaValidator cedulaValidator = new CedulaValidator();
    private final AmountValidator amountValidator = new AmountValidator();
    private final SalaryValidator salaryValidator = new SalaryValidator();
    private final TermValidator termValidator = new TermValidator();

    public List<String> validate(EvaluationRequest request) {
        List<String> errors = new ArrayList<>();
        
        cedulaValidator.validateField(request.getCedula(), "cédula", errors);
        amountValidator.validateField(request.getMontoSolicitado(), "monto solicitado", errors);
        salaryValidator.validateField(request.getSalario(), "salario", errors);
        termValidator.validateField(request.getTiempoAnios(), "plazo", errors);
        
        return errors;
    }
}