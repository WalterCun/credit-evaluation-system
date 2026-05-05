const PROVINCE_CODES = new Set([
  "01","02","03","04","05","06","07","08","09","10",
  "11","12","13","14","15","16","17","18","19","20",
  "21","22","23","24","25","26","27","28","29","30",
  "31",
]);

export function validateCedulaEcuadoriana(cedula: string): string | null {
  const clean = cedula.replace(/\D/g, "");

  if (clean.length !== 10) return "La cédula debe tener 10 dígitos";
  if (!/^\d{10}$/.test(clean)) return "La cédula debe contener solo dígitos";

  const province = clean.substring(0, 2);
  if (!PROVINCE_CODES.has(province)) {
    return `Código de provincia inválido (${province}). Debe estar entre 01 y 31`;
  }

  const thirdDigit = parseInt(clean.charAt(2), 10);
  if (thirdDigit > 5) {
    return "El tercer dígito debe ser 0-5 para personas naturales (6-9 solo para jurídicas)";
  }

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
