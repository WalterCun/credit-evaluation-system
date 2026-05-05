# Credit Evaluation System

Sistema de evaluación de crédito - Microservicios arquitectura distribuida

## 🚨 Arranque de Servicios (Docker - Recomendado)

```bash
# 1. Construir e iniciar todos los servicios
docker compose up -d --build

# 2. Verificar que todo esté corriendo
docker compose ps

# 3. Ver logs en tiempo real
docker compose logs -f

# 4. Acceder a la aplicación
# Frontend: http://localhost:5173
# Orchestrator API: http://localhost:8080
# Risk Service (HTTP): http://localhost:8081
# Risk Service (gRPC): localhost:9000
```

## 🛠️ Ejecución Local (Sin Docker)

### Prerrequisitos
- PostgreSQL ejecutándose en `localhost:5432`
- Java 21 (JDK)
- Maven 3.9+
- Node.js 18+ y npm

### Pasos

**1. Iniciar PostgreSQL (si no está corriendo)**
```bash
# Usar Docker solo para PostgreSQL
docker run -d --name postgres-credit \
  -e POSTGRES_DB=creditdb \
  -e POSTGRES_USER=credituser \
  -e POSTGRES_PASSWORD=creditpass \
  -p 5432:5432 \
  postgres:16-alpine

# Importar esquema
psql -U credituser -d creditdb -h localhost -f postgres/init.sql
```

**2. Risk Service**
```bash
cd risk-service
./mvnw quarkus:dev
# Windows: mvnw.cmd quarkus:dev
```
Puerto HTTP: 8081 | Puerto gRPC: 9000

**3. Orchestrator Service**
```bash
cd orchestrator-service
./mvnw quarkus:dev
# Windows: mvnw.cmd quarkus:dev
```
Puerto: 8080

**4. Frontend**
```bash
cd frontend
npm install
npm run dev
```
Puerto: 5173

## 🔍 Troubleshooting

### Los servicios Quarkus no arrancan

**Síntoma**: Error de conexión a PostgreSQL o al Risk Service

**Solución Docker**: Asegúrate de iniciar con `docker-compose up -d`. Los servicios esperan a PostgreSQL estar listo automáticamente.

**Solución Local**: Verifica que PostgreSQL esté escuchando en `localhost:5432` con:
```bash
psql -U credituser -d creditdb -h localhost -c "SELECT 1"
```

### gRPC connection refused

**Causa**: El puerto 9000 del risk-service no está accesible.

**En Docker**: Verifica que risk-service esté corriendo:
```bash
docker-compose ps risk-service
```

**En Local**: Verifica que risk-service esté escuchando en 9000:
```bash
netstat -ano | grep 9000
```

### Errores de validación al crear evaluación

**Causa**: Las validaciones del formulario fallan (formato de cédula, montos, etc.)

**Solución**: Verifica los datos de prueba:
```bash
curl -X POST http://localhost:8080/v1/credit-evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "cedula": "1234567890",
    "montoSolicitado": 10000,
    "salario": 5000,
    "tiempoAnios": 3
  }'
```

### Reconstruir servicios desde cero

```bash
# Stop everything
docker-compose down -v

# Clean build
cd orchestrator-service && ./mvnw clean && cd ..
cd risk-service && ./mvnw clean && cd ..
cd frontend && rm -rf node_modules dist && cd ..

# Rebuild and start
docker-compose up -d --build
```

## 📊 Puertos y Network

| Servicio | Puerto | Dentro Docker | Desde Host |
|----------|--------|---------------|------------|
| Frontend | 5173 | ❌ | ✅ http://localhost:5173 |
| Orchestrator (HTTP) | 8080 | ❌ | ✅ http://localhost:8080 |
| Risk Service (HTTP) | 8081 | ❌ | ✅ http://localhost:8081 |
| Risk Service (gRPC) | 9000 | ✅ | ✅ localhost:9000 |
| PostgreSQL | 5432 | ✅ | ✅ localhost:5432 |

**Nota**: Los servicios dentro del mismo Docker network se comunican por nombre de servicio:
- `postgres:5432` - PostgreSQL
- `risk-service:9000` - Risk Service gRPC
- `risk-service:8080` - Risk Service HTTP
- `orchestrator-service:8080` - Orchestrator HTTP

## 🐛 Verificación de Estado

```bash
# Check PostgreSQL
curl http://localhost:5432 2>&1 | head -1

# Check Orchestrator health
curl http://localhost:8080/v1/credit-evaluations

# Check Risk Service (si expone endpoint)
curl http://localhost:8081

# Test evaluation creation
curl -X POST http://localhost:8080/v1/credit-evaluations \
  -H "Content-Type: application/json" \
  -d '{"cedula":"1234567890","montoSolicitado":10000,"salario":5000,"tiempoAnios":2}'
```