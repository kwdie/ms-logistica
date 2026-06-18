package com.vetnova.ms_logistica.exception;

public class ProductoInventarioNoEncontradoException extends RuntimeException {

    public ProductoInventarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}