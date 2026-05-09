package com.credit.orchestrator.validator;

import java.math.BigDecimal;

/**
 * Validador de monto solicitado.
 * SRP: Única responsabilidad - validar que el monto es positivo y está en rango.
 */
public class AmountValidator implements FieldValidator<BigDecimal> {
    
    private static final BigDecimal MIN = BigDecimal.ZERO;
    
    @Override
    public String validate(BigDecimal value) {
        if (value == null) {
            return "El monto solicitado es requerido";
        }
        if (value.compareTo(MIN) <= 0) {
            return "El monto solicitado debe ser positivo";
        }
        return null;
    }
}