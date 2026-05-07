package com.vetnova.ms_logistica.exception;

public class ProveedorNoEncontradoException
        extends RuntimeException {

    public ProveedorNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}