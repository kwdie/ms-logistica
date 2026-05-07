package com.vetnova.ms_logistica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetnova.ms_logistica.model.Proveedor;

@Repository
public interface ProveedorRepository
        extends JpaRepository<Proveedor, Long> {

}