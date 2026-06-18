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

    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<String> manejarProveedorNoEncontrado(
            ProveedorNoEncontradoException ex) {

        logger.error("Proveedor no encontrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(SolicitudReposicionNoEncontradaException.class)
    public ResponseEntity<String> manejarSolicitudNoEncontrada(
            SolicitudReposicionNoEncontradaException ex) {

        logger.error("Solicitud no encontrada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProductoInventarioNoEncontradoException.class)
    public ResponseEntity<String> manejarProductoInventarioNoEncontrado(
            ProductoInventarioNoEncontradoException ex) {

        logger.error("Producto de inventario no encontrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<String> manejarReglaNegocio(
            ReglaNegocioException ex) {

        logger.error("Regla de negocio incumplida: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(ErrorComunicacionException.class)
    public ResponseEntity<String> manejarErrorComunicacion(
            ErrorComunicacionException ex) {

        logger.error("Error de comunicación: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarErrorGeneral(Exception ex) {

        logger.error("Error interno: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error interno: " + ex.getMessage());
    }
}