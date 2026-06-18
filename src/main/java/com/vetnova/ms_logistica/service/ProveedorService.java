package com.vetnova.ms_logistica.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.vetnova.ms_logistica.dto.ProveedorRequestDTO;
import com.vetnova.ms_logistica.dto.ProveedorResponseDTO;
import com.vetnova.ms_logistica.exception.ProveedorNoEncontradoException;
import com.vetnova.ms_logistica.exception.ReglaNegocioException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.repository.ProveedorRepository;

@Service
public class ProveedorService {

        private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

        private final ProveedorRepository repository;

        public ProveedorService(ProveedorRepository repository) {
                this.repository = repository;
        }

        public List<ProveedorResponseDTO> listar() {
                logger.info("Listando proveedores");

                return repository.findAll()
                                .stream()
                                .map(this::convertirAResponse)
                                .collect(Collectors.toList());
        }

        public ProveedorResponseDTO buscarPorId(Long id) {
                logger.info("Buscando proveedor con ID: " + id);

                Proveedor proveedor = obtenerProveedor(id);

                return convertirAResponse(proveedor);
        }

        public ProveedorResponseDTO guardar(ProveedorRequestDTO dto) {
                logger.info("Guardando proveedor: " + dto.getNombre());

                validarCorreoDuplicado(dto.getCorreo());

                Proveedor proveedor = new Proveedor();

                proveedor.setNombre(dto.getNombre());
                proveedor.setEmpresa(dto.getEmpresa());
                proveedor.setTelefono(dto.getTelefono());
                proveedor.setCorreo(dto.getCorreo());

                Proveedor proveedorGuardado = repository.save(proveedor);

                logger.info("Proveedor guardado con ID: " + proveedorGuardado.getId());

                return convertirAResponse(proveedorGuardado);
        }

        public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
                logger.info("Actualizando proveedor con ID: " + id);

                Proveedor proveedor = obtenerProveedor(id);

                validarCambioCorreoEnActualizacion(id, dto.getCorreo());

                proveedor.setNombre(dto.getNombre());
                proveedor.setEmpresa(dto.getEmpresa());
                proveedor.setTelefono(dto.getTelefono());
                proveedor.setCorreo(dto.getCorreo());

                Proveedor proveedorActualizado = repository.save(proveedor);

                logger.info("Proveedor actualizado con ID: " + proveedorActualizado.getId());

                return convertirAResponse(proveedorActualizado);
        }

        public void eliminar(Long id) {
                logger.info("Eliminando proveedor con ID: " + id);

                Proveedor proveedor = obtenerProveedor(id);

                repository.delete(proveedor);

                logger.info("Proveedor eliminado con ID: " + id);
        }

        public Proveedor obtenerProveedorEntidad(Long id) {
                return obtenerProveedor(id);
        }

        private Proveedor obtenerProveedor(Long id) {
                return repository.findById(id)
                                .orElseThrow(() -> {
                                        logger.error("Proveedor no encontrado con ID: " + id);

                                        return new ProveedorNoEncontradoException(
                                                        "Proveedor no encontrado");
                                });
        }

        private void validarCorreoDuplicado(String correo) {
                repository.findByCorreoIgnoreCase(correo)
                                .ifPresent(proveedor -> {
                                        logger.error("Correo duplicado: " + correo);

                                        throw new ReglaNegocioException(
                                                        "Ya existe un proveedor con ese correo");
                                });
        }

        private void validarCambioCorreoEnActualizacion(
                        Long proveedorId,
                        String correo) {

                repository.findByCorreoIgnoreCase(correo)
                                .ifPresent(proveedorExistente -> {
                                        if (!proveedorExistente.getId().equals(proveedorId)) {
                                                logger.error("Correo asociado a otro proveedor: " + correo);

                                                throw new ReglaNegocioException(
                                                                "Ya existe otro proveedor con ese correo");
                                        }
                                });
        }

        private ProveedorResponseDTO convertirAResponse(Proveedor proveedor) {
                return new ProveedorResponseDTO(
                                proveedor.getId(),
                                proveedor.getNombre(),
                                proveedor.getEmpresa(),
                                proveedor.getTelefono(),
                                proveedor.getCorreo());
        }
}