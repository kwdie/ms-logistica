package com.vetnova.ms_logistica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vetnova.ms_logistica.exception.ProveedorNoEncontradoException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.repository.ProveedorRepository;

@Service
public class ProveedorService {

        private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

        @Autowired
        private ProveedorRepository repository;

        // LISTAR PROVEEDORES
        public List<Proveedor> listar() {

                logger.info("Listando proveedores");

                return repository.findAll();
        }

        // BUSCAR PROVEEDOR POR ID
        public Proveedor buscarPorId(Long id) {

                logger.info("Buscando proveedor con ID: " + id);

                return repository.findById(id)
                                .orElseThrow(() -> {

                                        logger.error("Proveedor no encontrado con ID: " + id);

                                        return new ProveedorNoEncontradoException(
                                                        "Proveedor no encontrado");
                                });
        }

        // GUARDAR PROVEEDOR
        public Proveedor guardar(Proveedor proveedor) {

                logger.info("Guardando proveedor: "
                                + proveedor.getNombre());

                return repository.save(proveedor);
        }

        // ACTUALIZAR PROVEEDOR
        public Proveedor actualizar(
                        Long id,
                        Proveedor proveedorActualizado) {

                logger.info("Actualizando proveedor con ID: " + id);

                Proveedor proveedor = buscarPorId(id);
                proveedor.setNombre(
                                proveedorActualizado.getNombre());
                proveedor.setEmpresa(
                                proveedorActualizado.getEmpresa());
                proveedor.setTelefono(
                                proveedorActualizado.getTelefono());
                proveedor.setCorreo(
                                proveedorActualizado.getCorreo());
                return repository.save(proveedor);
        }

        // ELIMINAR PROVEEDOR
        public void eliminar(Long id) {

                logger.info("Eliminando proveedor con ID: " + id);

                Proveedor proveedor = buscarPorId(id);

                repository.delete(proveedor);
        }
}