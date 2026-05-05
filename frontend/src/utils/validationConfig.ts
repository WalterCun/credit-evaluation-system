/**
 * Configuración centralizada de límites de validación y utilidades para el formulario.
 *
 * Define los rangos permitidos para cada campo numérico del formulario de evaluación
 * y proporciona funciones auxiliares para formateo, sanitización y validación de inputs.
 */

/** Límites de validación para cada campo del formulario de evaluación crediticia. */
export const VALIDATION_LIMITS = {
  /** Rango permitido para el monto solicitado del crédito. */
  MONTO: {
    MIN: 500,
    MAX: 500000,
    MAX_DIGITS: 6,
    LABEL: "monto solicitado",
  },
  /** Rango permitido para el salario mensual del solicitante. */
  SALARIO: {
    MIN: 100,
    MAX: 100000,
    MAX_DIGITS: 6,
    LABEL: "salario mensual",
  },
  /** Rango permitido para el plazo del crédito en años. */
  PLAZO: {
    MIN: 1,
    MAX: 30,
    MAX_DIGITS: 2,
    LABEL: "plazo",
  },
} as const;

/**
 * Formatea un número como moneda ecuatoriana (separador de miles: coma, decimales: punto).
 *
 * @example
 * formatCurrency(10000) → "10.000,00"
 * formatCurrency(500)   → "500,00"
 */
export function formatCurrency(value: number | string): string {
  const num = typeof value === "string" ? parseFloat(value) : value;
  if (isNaN(num)) return "";
  return new Intl.NumberFormat("es-EC", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(num);
}

/**
 * Sanitiza un input numérico eliminando caracteres no numéricos y limitando la cantidad de dígitos.
 *
 * @param raw  - Valor crudo del input del usuario.
 * @param maxDigits - Cantidad máxima de dígitos permitidos.
 * @returns Objeto con el string sanitizado para display y el valor numérico parseado.
 *
 * @example
 * sanitizeNumericInput("1a0b0c0", 6) → { display: "1000", numeric: 1000 }
 */
export function sanitizeNumericInput(
  raw: string,
  maxDigits: number
): { display: string; numeric: number } {
  let cleaned = raw.replace(/[^0-9]/g, "");

  if (cleaned.length > maxDigits) {
    cleaned = cleaned.slice(0, maxDigits);
  }

  const numeric = parseInt(cleaned, 10);
  return {
    display: cleaned,
    numeric: isNaN(numeric) || cleaned.length === 0 ? 0 : numeric,
  };
}

/**
 * Valida que un valor numérico esté dentro del rango permitido.
 *
 * @param value - Valor numérico a validar.
 * @param min   - Valor mínimo permitido.
 * @param max   - Valor máximo permitido.
 * @param label - Etiqueta del campo (para personalizar el mensaje de error).
 * @returns Mensaje de error si la validación falla, o null si es válido.
 *
 * @example
 * validateNumericRange(50, 500, 500000, "monto solicitado") → "El monto solicitado mínimo es $500,00"
 * validateNumericRange(1000, 500, 500000, "monto solicitado") → null
 */
export function validateNumericRange(
  value: number,
  min: number,
  max: number,
  label: string
): string | null {
  if (value === 0) return `Ingrese un ${label} válido`;
  if (value < min) return `El ${label} mínimo es $${formatCurrency(min)}`;
  if (value > max) return `El ${label} máximo es $${formatCurrency(max)}`;
  return null;
}
