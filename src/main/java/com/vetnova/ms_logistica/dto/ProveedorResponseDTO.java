package com.vetnova.ms_logistica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponseDTO {

    private Long id;
    private String nombre;
    private String empresa;
    private String telefono;
    private String correo;
}