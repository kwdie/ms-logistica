package com.vetnova.ms_logistica.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ERROR PROVEEDOR NO ENCONTRADO
    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<String> manejarProveedorNoEncontrado(
            ProveedorNoEncontradoException ex) {

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

        return ResponseEntity.badRequest().body(errores);
    }

    // ERROR GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorGeneral(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error interno: "
                        + ex.getMessage());
    }
}