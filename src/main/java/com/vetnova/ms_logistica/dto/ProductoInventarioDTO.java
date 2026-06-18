package com.vetnova.ms_logistica.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoInventarioDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private int stock;
    private double precio;
}