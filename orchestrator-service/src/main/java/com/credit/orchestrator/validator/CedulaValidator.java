package com.credit.orchestrator.validator;

/**
 * Validador de cédula de identidad ecuatoriana (Módulo 10).
 *
 * Implementa el algoritmo del Registro Civil del Ecuador para verificar
 * la autenticidad de una cédula de identidad:
 *
 *   1. Debe tener exactamente 10 dígitos numéricos.
 *   2. Los primeros 2 dígitos (código de provincia) deben estar entre 01 y 24.
 *   3. Se aplica el algoritmo de verificación con coeficientes [2,1,2,1,2,1,2,1,2]:
 *      - Multiplicar cada dígito por su coeficiente.
 *      - Si el producto >= 10, restar 9.
 *      - Sumar todos los productos.
 *      - El décimo dígito debe ser igual a (10 - (suma % 10)) % 10.
 *
 * Nota: Esta implementación acepta provincias 01-24 (rango histórico).
 * El validador del frontend acepta 01-31 (rango ampliado con provincias recientes).
 *
 * @see com.credit.orchestrator.validator.CreditRequestValidator
 */
public class CedulaValidator {

    private CedulaValidator() {}

    /**
     * Verifica si una cédula ecuatoriana es válida según el algoritmo del Módulo 10.
     *
     * @param cedula String de 10 caracteres representando la cédula.
     * @return true si la cédula es válida, false en caso contrario.
     *
     * @example
     * isValid("1712345678") → true   (cédula válida con dígito verificador correcto)
     * isValid("0000000000") → false  (dígito verificador no coincide)
     * isValid("12345")     → false  (longitud incorrecta)
     */
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
