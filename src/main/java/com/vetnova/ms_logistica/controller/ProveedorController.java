package com.vetnova.ms_logistica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proveedores")
@Validated
public class ProveedorController {

    @Autowired
    private ProveedorService service;

    // LISTAR
    @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    // BUSCAR
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id));
    }

    // GUARDAR
    @PostMapping
    public ResponseEntity<Proveedor> guardar(
            @Valid @RequestBody Proveedor proveedor) {

        Proveedor nuevoProveedor =
                service.guardar(proveedor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevoProveedor);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Proveedor proveedor) {

        return ResponseEntity.ok(
                service.actualizar(id, proveedor));
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok(
                "Proveedor eliminado correctamente");
    }
}