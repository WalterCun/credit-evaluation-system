CREATE TABLE credit_evaluations (
    id BIGSERIAL PRIMARY KEY,
    cedula VARCHAR(10) NOT NULL,
    monto_solicitado DECIMAL(12,2) NOT NULL,
    salario DECIMAL(12,2) NOT NULL,
    tiempo_anios INTEGER NOT NULL,
    score INTEGER,
    deuda_mensual DECIMAL(12,2),
    estado VARCHAR(20) NOT NULL,
    fecha_evaluacion TIMESTAMPTZ DEFAULT NOW()
);
