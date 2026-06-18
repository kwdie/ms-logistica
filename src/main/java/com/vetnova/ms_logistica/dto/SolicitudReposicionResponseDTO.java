package com.vetnova.ms_logistica.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudReposicionResponseDTO {

    private Long id;
    private Long proveedorId;
    private String proveedor;
    private Long productoId;
    private String producto;
    private int cantidad;
    private String estado;
    private LocalDate fechaSolicitud;
}