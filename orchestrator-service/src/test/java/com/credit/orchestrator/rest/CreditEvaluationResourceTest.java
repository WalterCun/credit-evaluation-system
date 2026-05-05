package com.credit.orchestrator.rest;

import com.credit.orchestrator.model.EvaluationRequest;
import com.credit.orchestrator.model.EvaluationResult;
import com.credit.orchestrator.service.CreditEvaluationService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.smallrye.mutiny.Uni;

@QuarkusTest
@DisplayName("CreditEvaluationResource Tests")
class CreditEvaluationResourceTest {

    @InjectMock
    CreditEvaluationService creditEvaluationService;

    private EvaluationRequest validRequest;
    private EvaluationResult validResult;

    @BeforeEach
    void setUp() {
        validRequest = new EvaluationRequest();
        // Use a valid Ecuadorian cédula: 0102345675
        validRequest.setCedula("0102345675");
        validRequest.setMontoSolicitado(new BigDecimal("10000"));
        validRequest.setSalario(new BigDecimal("5000"));
        validRequest.setTiempoAnios(3);

        validResult = new EvaluationResult();
        validResult.setId(1L);
        validResult.setCedula("0102345675");
        validResult.setMontoSolicitado(new BigDecimal("10000"));
        validResult.setSalario(new BigDecimal("5000"));
        validResult.setTiempoAnios(3);
        validResult.setScore(75);
        validResult.setDeudaMensual(new BigDecimal("277.78"));
        validResult.setEstado("APROBADO");
        validResult.setFechaEvaluacion(OffsetDateTime.now());
    }

    @Nested
    @DisplayName("POST /v1/credit-evaluations Tests")
    class PostEvaluationTests {

        @Test
        @DisplayName("Should return 201 CREATED when request is valid")
        void shouldReturn201CreatedWhenRequestIsValid() {
            // Given
            when(creditEvaluationService.evaluate(any(EvaluationRequest.class)))
                    .thenReturn(Uni.createFrom().item(validResult));

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(201);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when cédula is invalid")
        void shouldReturn400BadRequestWhenCedulaIsInvalid() {
            // Given
            validRequest.setCedula("1234567890");

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400)
                    .body("size()", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when monto is null")
        void shouldReturn400BadRequestWhenMontoIsNull() {
            // Given - create fresh request to avoid side effects
            EvaluationRequest nullMontoRequest = new EvaluationRequest();
            nullMontoRequest.setCedula("0102345675");
            nullMontoRequest.setMontoSolicitado(null);
            nullMontoRequest.setSalario(new BigDecimal("5000"));
            nullMontoRequest.setTiempoAnios(3);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(nullMontoRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when monto is zero")
        void shouldReturn400BadRequestWhenMontoIsZero() {
            // Given
            validRequest.setMontoSolicitado(BigDecimal.ZERO);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when salario is null")
        void shouldReturn400BadRequestWhenSalarioIsNull() {
            // Given - create fresh request
            EvaluationRequest nullSalarioRequest = new EvaluationRequest();
            nullSalarioRequest.setCedula("0102345675");
            nullSalarioRequest.setMontoSolicitado(new BigDecimal("10000"));
            nullSalarioRequest.setSalario(null);
            nullSalarioRequest.setTiempoAnios(3);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(nullSalarioRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when tiempoAnios is null")
        void shouldReturn400BadRequestWhenTiempoAniosIsNull() {
            // Given - create fresh request
            EvaluationRequest nullTimeRequest = new EvaluationRequest();
            nullTimeRequest.setCedula("0102345675");
            nullTimeRequest.setMontoSolicitado(new BigDecimal("10000"));
            nullTimeRequest.setSalario(new BigDecimal("5000"));
            nullTimeRequest.setTiempoAnios(null);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(nullTimeRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when tiempoAnios is 0")
        void shouldReturn400BadRequestWhenTiempoAniosIs0() {
            // Given
            validRequest.setTiempoAnios(0);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST when tiempoAnios is greater than 30")
        void shouldReturn400BadRequestWhenTiempoAniosIsGreaterThan30() {
            // Given
            validRequest.setTiempoAnios(35);

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Should return multiple validation errors for completely invalid request")
        void shouldReturnMultipleValidationErrors() {
            // Given: Invalid request with multiple errors
            EvaluationRequest invalidRequest = new EvaluationRequest();
            invalidRequest.setCedula("123");           // Invalid length
            invalidRequest.setMontoSolicitado(null);     // Null
            invalidRequest.setSalario(BigDecimal.ZERO); // Zero
            invalidRequest.setTiempoAnios(100);        // Out of range

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(invalidRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(400)
                    .body("size()", is(4));
        }

        @Test
        @DisplayName("Should return response with evaluation result")
        void shouldReturnResponseWithEvaluationResult() {
            // Given
            when(creditEvaluationService.evaluate(any(EvaluationRequest.class)))
                    .thenReturn(Uni.createFrom().item(validResult));

            // When/Then
            RestAssured.given()
                    .contentType("application/json")
                    .body(validRequest)
                    .when()
                    .post("/v1/credit-evaluations")
                    .then()
                    .statusCode(201)
                    .body("cedula", is("0102345675"))
                    .body("estado", is("APROBADO"));
        }
    }

    @Nested
    @DisplayName("GET /v1/credit-evaluations Tests")
    class GetEvaluationsTests {

        @Test
        @DisplayName("Should return 200 OK when evaluations exist")
        void shouldReturn200OkWhenEvaluationsExist() {
            // Given
            when(creditEvaluationService.listAll())
                    .thenReturn(Uni.createFrom().item(List.of(validResult)));

            // When/Then
            RestAssured.when()
                    .get("/v1/credit-evaluations")
                    .then()
                    .statusCode(200)
                    .body("size()", is(1));
        }

        @Test
        @DisplayName("Should return empty list when no evaluations")
        void shouldReturnEmptyListWhenNoEvaluations() {
            // Given
            when(creditEvaluationService.listAll())
                    .thenReturn(Uni.createFrom().item(Collections.emptyList()));

            // When/Then
            RestAssured.when()
                    .get("/v1/credit-evaluations")
                    .then()
                    .statusCode(200)
                    .body("size()", is(0));
        }
    }
}