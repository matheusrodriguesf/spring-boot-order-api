package com.arceno.orderapi.exception;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        var response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Não encontrado",
                ex.getMessage(),
                Instant.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage()
                                : "Valor inválido",
                        (existing, replacement) -> existing));

        var response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "Erro de validação nos dados enviados",
                Instant.now().toString(),
                fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (existing, replacement) -> existing));

        var response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                "Erro de validação nos parâmetros da requisição",
                Instant.now().toString(),
                fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    public record ApiErrorResponse(
            int status,
            String error,
            String message,
            String timestamp) {
    }

    public record ValidationErrorResponse(
            int status,
            String error,
            String message,
            String timestamp,
            Map<String, String> fieldErrors) {
    }
}
