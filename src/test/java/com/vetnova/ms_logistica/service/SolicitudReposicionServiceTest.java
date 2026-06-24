package com.vetnova.ms_logistica.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.vetnova.ms_logistica.dto.ProductoInventarioDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionRequestDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionResponseDTO;
import com.vetnova.ms_logistica.exception.ReglaNegocioException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.model.SolicitudReposicion;
import com.vetnova.ms_logistica.repository.SolicitudReposicionRepository;

@ExtendWith(MockitoExtension.class)
class SolicitudReposicionServiceTest {

    @Mock
    private SolicitudReposicionRepository solicitudRepository;

    @Mock
    private ProveedorService proveedorService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    private SolicitudReposicionService solicitudService;

    @BeforeEach
    void setUp() {
        solicitudService = new SolicitudReposicionService(
                solicitudRepository,
                proveedorService,
                webClient
        );
    }

    @Test
    @DisplayName("Debe buscar una solicitud existente")
    void debeBuscarSolicitudPorId() {
        SolicitudReposicion solicitud = crearSolicitud(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE"
        );

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        SolicitudReposicionResponseDTO resultado = solicitudService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Pérez", resultado.getProveedor());
        assertEquals("Alimento Premium", resultado.getProducto());
        assertEquals(5, resultado.getCantidad());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(solicitudRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe guardar una solicitud con estado pendiente por defecto")
    void debeGuardarSolicitudPendiente() {
        SolicitudReposicionRequestDTO request = crearRequest(1L, 10L, 5, null);

        Proveedor proveedor = crearProveedor(1L, "Carlos Pérez");
        ProductoInventarioDTO producto = crearProductoInventario(10L, "Alimento Premium");

        SolicitudReposicion solicitudGuardada = crearSolicitud(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE"
        );

        when(proveedorService.obtenerProveedorEntidad(1L)).thenReturn(proveedor);

        when(webClient.get()
                .uri("http://localhost:8087/productos/" + 10L + "/dto")
                .retrieve()
                .bodyToMono(ProductoInventarioDTO.class))
                .thenReturn(Mono.just(producto));

        when(solicitudRepository.save(any(SolicitudReposicion.class)))
                .thenReturn(solicitudGuardada);

        SolicitudReposicionResponseDTO resultado = solicitudService.guardar(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Pérez", resultado.getProveedor());
        assertEquals("Alimento Premium", resultado.getProducto());
        assertEquals("PENDIENTE", resultado.getEstado());

        verify(proveedorService).obtenerProveedorEntidad(1L);
        verify(solicitudRepository).save(any(SolicitudReposicion.class));
    }

    @Test
    @DisplayName("No debe guardar una solicitud con estado inválido")
    void noDebeGuardarSolicitudConEstadoInvalido() {
        SolicitudReposicionRequestDTO request = crearRequest(1L, 10L, 5, "ENVIADA");

        Proveedor proveedor = crearProveedor(1L, "Carlos Pérez");
        ProductoInventarioDTO producto = crearProductoInventario(10L, "Alimento Premium");

        when(proveedorService.obtenerProveedorEntidad(1L)).thenReturn(proveedor);

        when(webClient.get()
                .uri("http://localhost:8087/productos/" + 10L + "/dto")
                .retrieve()
                .bodyToMono(ProductoInventarioDTO.class))
                .thenReturn(Mono.just(producto));

        assertThrows(
                ReglaNegocioException.class,
                () -> solicitudService.guardar(request)
        );

        verify(solicitudRepository, never()).save(any(SolicitudReposicion.class));
    }

    @Test
    @DisplayName("Debe actualizar una solicitud existente")
    void debeActualizarSolicitud() {
        SolicitudReposicion solicitudExistente = crearSolicitud(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE"
        );

        SolicitudReposicionRequestDTO request = crearRequest(2L, 11L, 8, "RECIBIDA");

        Proveedor proveedor = crearProveedor(2L, "María López");
        ProductoInventarioDTO producto = crearProductoInventario(11L, "Shampoo Medicado");

        SolicitudReposicion solicitudActualizada = crearSolicitud(
                1L,
                2L,
                "María López",
                11L,
                "Shampoo Medicado",
                8,
                "RECIBIDA"
        );

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitudExistente));
        when(proveedorService.obtenerProveedorEntidad(2L)).thenReturn(proveedor);

        when(webClient.get()
                .uri("http://localhost:8087/productos/" + 11L + "/dto")
                .retrieve()
                .bodyToMono(ProductoInventarioDTO.class))
                .thenReturn(Mono.just(producto));

        when(solicitudRepository.save(any(SolicitudReposicion.class)))
                .thenReturn(solicitudActualizada);

        SolicitudReposicionResponseDTO resultado = solicitudService.actualizar(1L, request);

        assertEquals(1L, resultado.getId());
        assertEquals("María López", resultado.getProveedor());
        assertEquals("Shampoo Medicado", resultado.getProducto());
        assertEquals(8, resultado.getCantidad());
        assertEquals("RECIBIDA", resultado.getEstado());

        verify(solicitudRepository).findById(1L);
        verify(solicitudRepository).save(any(SolicitudReposicion.class));
    }

    @Test
    @DisplayName("Debe eliminar una solicitud existente")
    void debeEliminarSolicitud() {
        SolicitudReposicion solicitud = crearSolicitud(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE"
        );

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        solicitudService.eliminar(1L);

        verify(solicitudRepository).findById(1L);
        verify(solicitudRepository).delete(solicitud);
    }

    private SolicitudReposicion crearSolicitud(
            Long id,
            Long proveedorId,
            String proveedor,
            Long productoId,
            String producto,
            int cantidad,
            String estado
    ) {
        SolicitudReposicion solicitud = new SolicitudReposicion();
        solicitud.setId(id);
        solicitud.setProveedorId(proveedorId);
        solicitud.setNombreProveedor(proveedor);
        solicitud.setProductoId(productoId);
        solicitud.setNombreProducto(producto);
        solicitud.setCantidad(cantidad);
        solicitud.setEstado(estado);
        solicitud.setFechaSolicitud(LocalDate.now());
        return solicitud;
    }

    private SolicitudReposicionRequestDTO crearRequest(
            Long proveedorId,
            Long productoId,
            int cantidad,
            String estado
    ) {
        SolicitudReposicionRequestDTO request = new SolicitudReposicionRequestDTO();
        request.setProveedorId(proveedorId);
        request.setProductoId(productoId);
        request.setCantidad(cantidad);
        request.setEstado(estado);
        return request;
    }

    private Proveedor crearProveedor(Long id, String nombre) {
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre(nombre);
        proveedor.setEmpresa("Empresa de prueba");
        proveedor.setTelefono("912345678");
        proveedor.setCorreo("proveedor@test.cl");
        return proveedor;
    }

    private ProductoInventarioDTO crearProductoInventario(Long id, String nombre) {
        ProductoInventarioDTO producto = new ProductoInventarioDTO();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setCategoria("Categoría de prueba");
        producto.setStock(20);
        producto.setPrecio(12990);
        return producto;
    }
}