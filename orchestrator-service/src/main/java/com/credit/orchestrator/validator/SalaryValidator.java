package com.credit.orchestrator.validator;

import java.math.BigDecimal;

/**
 * Validador de salario.
 * SRP: Única responsabilidad - validar que el salario es positivo.
 */
public class SalaryValidator implements FieldValidator<BigDecimal> {
    
    private static final BigDecimal MIN = BigDecimal.ZERO;
    
    @Override
    public String validate(BigDecimal value) {
        if (value == null) {
            return "El salario es requerido";
        }
        if (value.compareTo(MIN) <= 0) {
            return "El salario debe ser positivo";
        }
        return null;
    }
}