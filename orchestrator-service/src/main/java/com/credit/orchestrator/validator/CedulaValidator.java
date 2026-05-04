package com.credit.orchestrator.validator;

public class CedulaValidator {

    private CedulaValidator() {}

    public static boolean isValid(String cedula) {
        if (cedula == null || cedula.length() != 10) {
            return false;
        }
        if (!cedula.matches("\\d{10}")) {
            return false;
        }
        int provinceCode = Integer.parseInt(cedula.substring(0, 2));
        if (provinceCode < 1 || provinceCode > 24) {
            return false;
        }
        int[] coefficients = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int digit = Character.getNumericValue(cedula.charAt(i));
            int product = digit * coefficients[i];
            if (product >= 10) {
                product -= 9;
            }
            sum += product;
        }
        int verifier = Character.getNumericValue(cedula.charAt(9));
        int remainder = sum % 10;
        int expected = (remainder == 0) ? 0 : 10 - remainder;
        return verifier == expected;
    }
}
