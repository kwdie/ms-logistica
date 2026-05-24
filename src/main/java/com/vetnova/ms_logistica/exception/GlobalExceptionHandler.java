package com.vetnova.ms_logistica.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ERROR PROVEEDOR NO ENCONTRADO
    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<String> manejarProveedorNoEncontrado(
            ProveedorNoEncontradoException ex) {

        logger.error("Error: " + ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // ERRORES DE VALIDACIÓN
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {

            errores.put(
                    error.getField(),
                    error.getDefaultMessage());
        });

        logger.error("Error de validación");
        return ResponseEntity.badRequest().body(errores);
    }

    // ERROR GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorGeneral(
            Exception ex) {
        logger.error("Error interno: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error interno: "
                        + ex.getMessage());
    }
}