package com.ecommerce.catalog.handler;

import com.ecommerce.catalog.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;

//anotacion de @Slf4j de lombok para ver los logs (log.warn, log.error, log.info, log.debug)
// es una forma de hacer una buena practica de logs 
@Slf4j
// @ControllerAdvice esto es clave por que le dice a Spring que esto debe hacer
// una intercepcion
// y procesar los excepciones que puedad ocurrir
@ControllerAdvice
public class GlobalExceptionHandler {

    // maneja el codigo 409 conflict esto captura errores en la DB como
    // restricciones (unique)
    // y me evito que la DB me envie un error crudo

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // recordar que un warn es suficiente para dar un error en la entrada del
        // usuario
        log.warn("Data integrity violation detected: {}", ex.getMessage());

        Map<String, String> errorDetails = new HashMap<>();
        // msm generico DataIntegrityViolationException pero puede ser por fk o unque
        // msm un compromiso para el caso de uso de SKU Duplicado
        String userFriendlyMessage = "Error de datos: El SKU, nombre, o algún otro campo único ya está en uso.";

        errorDetails.put("error", "Conflicto de Integridad de Datos");
        errorDetails.put("message", userFriendlyMessage);
        // 409 conflict esto es semi correcto para indicar que hubo un error en la DB
        // que recurso que es intenta crear ya existe y hay conflictos
        //
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT); // 409
    }

    // (Código 404 NOT FOUND) captura el error 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for request: {} validation errors", ex.getBindingResult().getErrorCount());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
            log.debug("Validation error - field: {}, message: {}", error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Ocurrió un error del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
