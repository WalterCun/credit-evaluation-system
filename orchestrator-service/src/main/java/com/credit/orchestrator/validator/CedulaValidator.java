package com.credit.orchestrator.validator;

import java.util.List;

/**
 * Validador de cédula ecuatoriana.
 * 
 * SRP: Única responsabilidad - validar cédula.
 * OCP: Extiende mediante el método validateField.
 */
public class CedulaValidator implements FieldValidator<String> {
    
    private static final int MIN_PROVINCE = 1;
    private static final int MAX_PROVINCE = 31;
    private static final int[] COEFFICIENTS = {2, 1, 2, 1, 2, 1, 2, 1, 2};

    @Override
    public String validate(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return "Cédula ecuatoriana inválida (10 dígitos, módulo 10)";
        }
        if (!cedula.matches("\\d{10}")) {
            return "Cédula ecuatoriana inválida (10 dígitos, módulo 10)";
        }
        int provinceCode = Integer.parseInt(cedula.substring(0, 2));
        if (provinceCode < MIN_PROVINCE || provinceCode > MAX_PROVINCE) {
            return "Cédula ecuatoriana inválida (código de provincia no válido)";
        }
        int thirdDigit = Character.getNumericValue(cedula.charAt(2));
        if (thirdDigit > 5) {
            return "Cédula ecuatoriana inválida (tercer dígito debe ser 0-5)";
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(cedula.charAt(i));
            int product = digit * COEFFICIENTS[i];
            if (product >= 10) {
                product -= 9;
            }
            sum += product;
        }
        int verifier = Character.getNumericValue(cedula.charAt(9));
        int expected = (sum % 10 == 0) ? 0 : 10 - (sum % 10);
        if (verifier != expected) {
            return "Cédula ecuatoriana inválida (dígito verificador no coincide)";
        }
        return null;
    }
    
    public void validateField(String value, String fieldName, List<String> errors) {
        String error = validate(value);
        if (error != null) {
            errors.add(error);
        }
    }

    public static boolean isValid(String cedula) {
        return new CedulaValidator().validate(cedula) == null;
    }
}