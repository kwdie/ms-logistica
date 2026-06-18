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

import com.vetnova.ms_logistica.dto.ProveedorRequestDTO;
import com.vetnova.ms_logistica.dto.ProveedorResponseDTO;
import com.vetnova.ms_logistica.service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

        private final ProveedorService service;

        public ProveedorController(ProveedorService service) {
                this.service = service;
        }

        @GetMapping
        public ResponseEntity<List<ProveedorResponseDTO>> listar() {
                return ResponseEntity.ok(service.listar());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProveedorResponseDTO> buscar(
                        @PathVariable Long id) {

                return ResponseEntity.ok(service.buscarPorId(id));
        }

        @PostMapping
        public ResponseEntity<ProveedorResponseDTO> guardar(
                        @Valid @RequestBody ProveedorRequestDTO dto) {

                return new ResponseEntity<>(
                                service.guardar(dto),
                                HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ProveedorResponseDTO> actualizar(
                        @PathVariable Long id,
                        @Valid @RequestBody ProveedorRequestDTO dto) {

                return ResponseEntity.ok(service.actualizar(id, dto));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> eliminar(
                        @PathVariable Long id) {

                service.eliminar(id);

                return ResponseEntity.ok(
                                "Proveedor eliminado correctamente");
        }
}