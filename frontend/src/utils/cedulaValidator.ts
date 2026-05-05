/**
 * Validador de cédula de identidad ecuatoriana.
 *
 * Implementa el algoritmo del dígito verificador (Módulo 10) definido
 * por el Registro Civil del Ecuador. Validaciones realizadas:
 *
 *   1. Longitud exacta de 10 dígitos numéricos.
 *   2. Código de provincia válido (01-31).
 *   3. Tercer dígito entre 0 y 5 (personas naturales; 6-9 son jurídicas).
 *   4. Dígito verificador (posición 10) calculado con coeficientes [2,1,2,1,2,1,2,1,2].
 *
 * @example
 * validateCedulaEcuadoriana("1712345678") → null  (válida)
 * validateCedulaEcuadoriana("0000000000") → "Cédula no válida. El dígito verificador no coincide"
 */

/** Códigos de provincia válidos según el Registro Civil del Ecuador (01-31). */
const PROVINCE_CODES = new Set([
  "01","02","03","04","05","06","07","08","09","10",
  "11","12","13","14","15","16","17","18","19","20",
  "21","22","23","24","25","26","27","28","29","30",
  "31",
]);

/**
 * Valida una cédula ecuatoriana usando el algoritmo del Módulo 10.
 *
 * @param cedula - String de 10 caracteres representando la cédula.
 * @returns Mensaje de error si la cédula es inválida, o null si es válida.
 */
export function validateCedulaEcuadoriana(cedula: string): string | null {
  const clean = cedula.replace(/\D/g, "");

  if (clean.length !== 10) return "La cédula debe tener 10 dígitos";
  if (!/^\d{10}$/.test(clean)) return "La cédula debe contener solo dígitos";

  /** Validación 1: código de provincia (primeros 2 dígitos). */
  const province = clean.substring(0, 2);
  if (!PROVINCE_CODES.has(province)) {
    return `Código de provincia inválido (${province}). Debe estar entre 01 y 31`;
  }

  /** Validación 2: tercer dígito (0-5 para personas naturales). */
  const thirdDigit = parseInt(clean.charAt(2), 10);
  if (thirdDigit > 5) {
    return "El tercer dígito debe ser 0-5 para personas naturales (6-9 solo para jurídicas)";
  }

  /**
   * Validación 3: dígito verificador (Módulo 10).
   * Se multiplican los primeros 9 dígitos por los coeficientes [2,1,2,1,2,1,2,1,2].
   * Si el producto es >= 10, se resta 9. Luego se suma todo y se compara
   * el décimo dígito con (10 - (suma % 10)) % 10.
   */
  const coefficients = [2, 1, 2, 1, 2, 1, 2, 1, 2];
  let sum = 0;

  for (let i = 0; i < 9; i++) {
    const product = parseInt(clean.charAt(i), 10) * coefficients[i]!;
    sum += product >= 10 ? product - 9 : product;
  }

  const verifier = parseInt(clean.charAt(9), 10);
  const expectedVerifier = (10 - (sum % 10)) % 10;

  if (verifier !== expectedVerifier) {
    return "Cédula no válida. El dígito verificador no coincide";
  }

  return null;
}
