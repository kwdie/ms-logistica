package com.vetnova.ms_logistica.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitudes_reposicion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudReposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long proveedorId;

    private String nombreProveedor;

    private Long productoId;

    private String nombreProducto;

    private int cantidad;

    private String estado;

    private LocalDate fechaSolicitud;

    @PrePersist
    public void asignarFechaSolicitud() {
        this.fechaSolicitud = LocalDate.now();
    }
}