# Pruebas Unitarias - Credit Evaluation System

Este documento describe la estructura de pruebas unitarias para los microservicios `orchestrator-service` y `risk-service`.

## Requisitos

- Java 21
- Maven 3.9+
- Quarkus 3.18.4

## Estructura de Pruebas

### orchestrator-service

```
src/test/java/com/credit/orchestrator/
├── rest/
│   └── CreditEvaluationResourceTest.java     # Pruebas del controlador REST
├── service/
│   ├── CreditEvaluationServiceTest.java       # Pruebas del servicio principal
│   └── RiskClientTest.java                    # Pruebas del cliente gRPC
├── model/
│   ├── CreditEvaluationTest.java              # Pruebas de la entidad
│   └── EvaluationResultTest.java              # Pruebas del DTO de respuesta
└── validator/
    ├── CedulaValidatorTest.java               # Pruebas del validador de cédula
    └── CreditRequestValidatorTest.java        # Pruebas del validador de solicitud
```

### risk-service

```
src/test/java/com/credit/risk/
├── grpc/
│   └── GrpcRiskServiceTest.java               # Pruebas del servicio gRPC
└── service/
    └── RiskServiceTest.java                   # Pruebas del servicio de riesgo
```

## Dependencias de Test (pom.xml)

```xml
<!-- Test dependencies -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5-mockito</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.smallrye</groupId>
    <artifactId>smallrye-context-propagation</artifactId>
    <scope>test</scope>
</dependency>
```

## Ejecución de Pruebas

### Ejecutar todas las pruebas

```bash
# Desde el directorio raíz del proyecto
mvn clean test
```

### Ejecutar pruebas de un servicio específico

```bash
# Ejecutar solo pruebas del orchestrator-service
cd orchestrator-service
mvn test

# Ejecutar solo pruebas del risk-service
cd risk-service
mvn test
```

### Ejecutar una clase de prueba específica

```bash
# Ejecutar una prueba específica
mvn test -Dtest=CedulaValidatorTest
mvn test -Dtest=RiskServiceTest
mvn test -Dtest=CreditEvaluationResourceTest
```

### Ejecutar con cobertura

```bash
mvn test jacoco:report
```

## Patrones de Pruebas Implementados

### Given-When-Then (Arrange-Act-Assert)

Todos los métodos de prueba siguen el patrón Given-When-Then:

```java
@Test
void shouldReturnAprobadoWhenScoreAbove70AndTotalMensualBelowLimit() {
    // Given: Configurar el estado inicial y mocks
    RiskScoreResponse scoreResponse = RiskScoreResponse.newBuilder()
            .setCedula("1712345678")
            .setScore(75)
            .build();
    
    // When: Ejecutar la operación bajo prueba
    EvaluationResult result = service.evaluate(request).await().indefinitely();
    
    // Then: Verificar el resultado esperado
    assertEquals("APROBADO", result.getEstado());
}
```

### Nomenclatura de Métodos

Los nombres de los métodos siguen el patrón `should[ExpectedResult]When[Condition]`:

- `shouldReturn201CreatedWhenRequestIsValid`
- `shouldReturnBadRequestWhenCedulaIsInvalid`
- `shouldReturnAprobadoWhenScoreAbove70AndTotalMensualBelowLimit`

### Casos de Prueba Cubiertos

1. **Casos felices (Happy Path)**
   - Solicitud válida con todos los campos correctos
   - Cálculo de score dentro del rango esperado
   - Persistencia exitosa de la evaluación

2. **Casos alternativos (Edge Cases)**
   - Valores límite (tiempo = 1, tiempo = 30)
   - Listas vacías
   - Decimales y redondeos

3. **Casos de error (Error Cases)**
   - Cédula inválida (longitud incorrecta, dígito verificador erróneo)
   - Monto negativo o cero
   - Salario negativo o cero
   - Tiempo fuera de rango

## Mocking

### Mockito con @ExtendWith

Para pruebas unitarias de servicios:

```java
@ExtendWith(MockitoExtension.class)
class CreditEvaluationServiceTest {
    @Mock
    RiskClient riskClient;
    
    @InjectMocks
    CreditEvaluationService service;
}
```

### InjectMock con Quarkus

Para pruebas de integración con @QuarkusTest:

```java
@QuarkusTest
class CreditEvaluationResourceTest {
    @InjectMock
    CreditEvaluationService creditEvaluationService;
}
```

## Pruebas REST con RestAssured

```java
@QuarkusTest
class CreditEvaluationResourceTest {
    @Test
    void shouldReturn201CreatedWhenRequestIsValid() {
        RestAssured.given()
            .contentType("application/json")
            .body(validRequest)
            .when()
            .post("/v1/credit-evaluations")
            .then()
            .statusCode(201);
    }
}
```

## Notas Adicionales

- Las pruebas usan `Uni.await().indefinitely()` para obtener resultados de manera síncrona en tests reactivos
- Los tests de gRPC utilizan mocks del stub generado por Quarkus
- La entrada/salida JSON es validada implícitamente por la serialización de Jackson