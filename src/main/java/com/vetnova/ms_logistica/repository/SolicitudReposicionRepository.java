package com.vetnova.ms_logistica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.ms_logistica.model.SolicitudReposicion;

@Repository
public interface SolicitudReposicionRepository
        extends JpaRepository<SolicitudReposicion, Long> {

    List<SolicitudReposicion> findByProveedorId(Long proveedorId);
}