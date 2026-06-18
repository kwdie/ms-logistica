package com.vetnova.ms_logistica.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetnova.ms_logistica.dto.SolicitudReposicionRequestDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionResponseDTO;
import com.vetnova.ms_logistica.service.SolicitudReposicionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/solicitudes-reposicion")
public class SolicitudReposicionController {

    private final SolicitudReposicionService service;

    public SolicitudReposicionController(SolicitudReposicionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudReposicionResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudReposicionResponseDTO> buscar(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SolicitudReposicionResponseDTO> guardar(
            @Valid @RequestBody SolicitudReposicionRequestDTO dto) {

        return new ResponseEntity<>(
                service.guardar(dto),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudReposicionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudReposicionRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok(
                "Solicitud de reposición eliminada correctamente");
    }
}