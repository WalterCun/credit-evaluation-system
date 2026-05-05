export const VALIDATION_LIMITS = {
  MONTO: {
    MIN: 500,
    MAX: 500000,
    MAX_DIGITS: 6,
    LABEL: "monto solicitado",
  },
  SALARIO: {
    MIN: 100,
    MAX: 100000,
    MAX_DIGITS: 6,
    LABEL: "salario mensual",
  },
  PLAZO: {
    MIN: 1,
    MAX: 30,
    MAX_DIGITS: 2,
    LABEL: "plazo",
  },
} as const;

export function formatCurrency(value: number | string): string {
  const num = typeof value === "string" ? parseFloat(value) : value;
  if (isNaN(num)) return "";
  return new Intl.NumberFormat("es-EC", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(num);
}

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
