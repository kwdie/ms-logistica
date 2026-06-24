package com.vetnova.ms_logistica.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetnova.ms_logistica.dto.ProveedorRequestDTO;
import com.vetnova.ms_logistica.dto.ProveedorResponseDTO;
import com.vetnova.ms_logistica.exception.ProveedorNoEncontradoException;
import com.vetnova.ms_logistica.exception.ReglaNegocioException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    private ProveedorService proveedorService;

    @BeforeEach
    void setUp() {
        proveedorService = new ProveedorService(proveedorRepository);
    }

    @Test
    @DisplayName("Debe listar los proveedores registrados")
    void debeListarProveedores() {
        Proveedor proveedor1 = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        Proveedor proveedor2 = crearProveedor(
                2L,
                "María López",
                "Insumos Vet",
                "987654321",
                "ventas@insumosvet.cl"
        );

        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor1, proveedor2));

        List<ProveedorResponseDTO> resultado = proveedorService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Carlos Pérez", resultado.get(0).getNombre());
        assertEquals("PetFood Chile", resultado.get(0).getEmpresa());
        assertEquals("María López", resultado.get(1).getNombre());

        verify(proveedorRepository).findAll();
    }

    @Test
    @DisplayName("Debe buscar un proveedor existente por ID")
    void debeBuscarProveedorPorId() {
        Proveedor proveedor = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        ProveedorResponseDTO resultado = proveedorService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Pérez", resultado.getNombre());
        assertEquals("PetFood Chile", resultado.getEmpresa());
        assertEquals("912345678", resultado.getTelefono());
        assertEquals("contacto@petfood.cl", resultado.getCorreo());

        verify(proveedorRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar error si el proveedor no existe")
    void debeLanzarErrorSiProveedorNoExiste() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ProveedorNoEncontradoException.class,
                () -> proveedorService.buscarPorId(99L)
        );

        verify(proveedorRepository).findById(99L);
    }

    @Test
    @DisplayName("Debe guardar un proveedor nuevo")
    void debeGuardarProveedorNuevo() {
        ProveedorRequestDTO request = crearRequest(
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        Proveedor proveedorGuardado = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        when(proveedorRepository.findByCorreoIgnoreCase("contacto@petfood.cl"))
                .thenReturn(Optional.empty());

        when(proveedorRepository.save(any(Proveedor.class)))
                .thenReturn(proveedorGuardado);

        ProveedorResponseDTO resultado = proveedorService.guardar(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Pérez", resultado.getNombre());
        assertEquals("PetFood Chile", resultado.getEmpresa());
        assertEquals("contacto@petfood.cl", resultado.getCorreo());

        verify(proveedorRepository).findByCorreoIgnoreCase("contacto@petfood.cl");
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("No debe guardar un proveedor con correo duplicado")
    void noDebeGuardarProveedorConCorreoDuplicado() {
        Proveedor proveedorExistente = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        ProveedorRequestDTO request = crearRequest(
                "Otro Proveedor",
                "Otra Empresa",
                "987654321",
                "contacto@petfood.cl"
        );

        when(proveedorRepository.findByCorreoIgnoreCase("contacto@petfood.cl"))
                .thenReturn(Optional.of(proveedorExistente));

        assertThrows(
                ReglaNegocioException.class,
                () -> proveedorService.guardar(request)
        );

        verify(proveedorRepository).findByCorreoIgnoreCase("contacto@petfood.cl");
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debe actualizar un proveedor existente")
    void debeActualizarProveedor() {
        Proveedor proveedorExistente = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        ProveedorRequestDTO request = crearRequest(
                "Carlos Pérez Actualizado",
                "PetFood Chile SpA",
                "923456789",
                "nuevo@petfood.cl"
        );

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedorExistente));

        when(proveedorRepository.findByCorreoIgnoreCase("nuevo@petfood.cl"))
                .thenReturn(Optional.empty());

        when(proveedorRepository.save(any(Proveedor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProveedorResponseDTO resultado = proveedorService.actualizar(1L, request);

        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Pérez Actualizado", resultado.getNombre());
        assertEquals("PetFood Chile SpA", resultado.getEmpresa());
        assertEquals("923456789", resultado.getTelefono());
        assertEquals("nuevo@petfood.cl", resultado.getCorreo());

        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("No debe actualizar un proveedor inexistente")
    void noDebeActualizarProveedorInexistente() {
        ProveedorRequestDTO request = crearRequest(
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        when(proveedorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProveedorNoEncontradoException.class,
                () -> proveedorService.actualizar(99L, request)
        );

        verify(proveedorRepository).findById(99L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("No debe actualizar si el correo pertenece a otro proveedor")
    void noDebeActualizarConCorreoDeOtroProveedor() {
        Proveedor proveedorActual = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        Proveedor otroProveedor = crearProveedor(
                2L,
                "María López",
                "Insumos Vet",
                "987654321",
                "ventas@insumosvet.cl"
        );

        ProveedorRequestDTO request = crearRequest(
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "ventas@insumosvet.cl"
        );

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedorActual));

        when(proveedorRepository.findByCorreoIgnoreCase("ventas@insumosvet.cl"))
                .thenReturn(Optional.of(otroProveedor));

        assertThrows(
                ReglaNegocioException.class,
                () -> proveedorService.actualizar(1L, request)
        );

        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debe eliminar un proveedor existente")
    void debeEliminarProveedor() {
        Proveedor proveedor = crearProveedor(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(proveedor));

        proveedorService.eliminar(1L);

        verify(proveedorRepository).findById(1L);
        verify(proveedorRepository).delete(proveedor);
    }

    @Test
    @DisplayName("No debe eliminar un proveedor inexistente")
    void noDebeEliminarProveedorInexistente() {
        when(proveedorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProveedorNoEncontradoException.class,
                () -> proveedorService.eliminar(99L)
        );

        verify(proveedorRepository).findById(99L);
        verify(proveedorRepository, never()).delete(any(Proveedor.class));
    }

    private Proveedor crearProveedor(
            Long id,
            String nombre,
            String empresa,
            String telefono,
            String correo
    ) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre(nombre);
        proveedor.setEmpresa(empresa);
        proveedor.setTelefono(telefono);
        proveedor.setCorreo(correo);
        return proveedor;
    }

    private ProveedorRequestDTO crearRequest(
            String nombre,
            String empresa,
            String telefono,
            String correo
    ) {
        ProveedorRequestDTO request = new ProveedorRequestDTO();
        request.setNombre(nombre);
        request.setEmpresa(empresa);
        request.setTelefono(telefono);
        request.setCorreo(correo);
        return request;
    }
}