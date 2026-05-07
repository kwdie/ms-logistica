package com.vetnova.ms_logistica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetnova.ms_logistica.exception.ProveedorNoEncontradoException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.repository.ProveedorRepository;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repository;

    // LISTAR
    public List<Proveedor> listar() {

        return repository.findAll();
    }

    // BUSCAR POR ID
    public Proveedor buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ProveedorNoEncontradoException(
                                "Proveedor no encontrado"));
    }

    // GUARDAR
    public Proveedor guardar(Proveedor proveedor) {

        return repository.save(proveedor);
    }

    // ACTUALIZAR
    public Proveedor actualizar(Long id,
                                Proveedor proveedor) {

        Proveedor existente = buscarPorId(id);

        existente.setNombre(proveedor.getNombre());
        existente.setEmpresa(proveedor.getEmpresa());
        existente.setTelefono(proveedor.getTelefono());
        existente.setCorreo(proveedor.getCorreo());

        return repository.save(existente);
    }

    // ELIMINAR
    public void eliminar(Long id) {

        Proveedor proveedor = buscarPorId(id);

        repository.delete(proveedor);
    }
}