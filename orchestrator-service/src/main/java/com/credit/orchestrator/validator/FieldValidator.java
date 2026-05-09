package com.credit.orchestrator.validator;

import java.util.List;

/**
 * Interfaz para validadores de campos de solicitud crediticia.
 * 
 * ISP: Interfaz segregada con única responsabilidad.
 */
public interface FieldValidator<T> {
    /**
     * Valida el valor proporcionado.
     * 
     * @param value Valor a validar
     * @return Mensaje de error o null si es válido
     */
    String validate(T value);
    
    /**
     * Agrega error a la lista si la validación falla.
     */
    default void validateField(T value, String fieldName, List<String> errors) {
        String error = validate(value);
        if (error != null) {
            errors.add(error);
        }
    }
}