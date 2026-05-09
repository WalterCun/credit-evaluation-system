package com.credit.orchestrator.validator;

import java.util.List;

/**
 * Validador de plazo del crédito.
 * SRP: Única responsabilidad - validar que el plazo está en rango válido.
 */
public class TermValidator implements FieldValidator<Integer> {
    
    private static final int MIN_YEARS = 1;
    private static final int MAX_YEARS = 30;

    @Override
    public String validate(Integer value) {
        if (value == null) {
            return "El tiempo es requerido";
        }
        if (value < MIN_YEARS || value > MAX_YEARS) {
            return "El tiempo debe estar entre 1 y 30 años";
        }
        return null;
    }
    
    public void validateField(Integer value, String fieldName, List<String> errors) {
        String error = validate(value);
        if (error != null) {
            errors.add(error);
        }
    }
}