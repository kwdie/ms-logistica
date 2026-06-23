package com.vetnova.ms_logistica.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.vetnova.ms_logistica.dto.ProductoInventarioDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionRequestDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionResponseDTO;
import com.vetnova.ms_logistica.exception.ErrorComunicacionException;
import com.vetnova.ms_logistica.exception.ProductoInventarioNoEncontradoException;
import com.vetnova.ms_logistica.exception.ReglaNegocioException;
import com.vetnova.ms_logistica.exception.SolicitudReposicionNoEncontradaException;
import com.vetnova.ms_logistica.model.Proveedor;
import com.vetnova.ms_logistica.model.SolicitudReposicion;
import com.vetnova.ms_logistica.repository.SolicitudReposicionRepository;

@Service
public class SolicitudReposicionService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudReposicionService.class);

    private final SolicitudReposicionRepository repository;
    private final ProveedorService proveedorService;
    private final WebClient webClient;

    public SolicitudReposicionService(
            SolicitudReposicionRepository repository,
            ProveedorService proveedorService,
            WebClient webClient) {

        this.repository = repository;
        this.proveedorService = proveedorService;
        this.webClient = webClient;
    }

    public List<SolicitudReposicionResponseDTO> listar() {
        logger.info("Listando solicitudes de reposición");

        return repository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public SolicitudReposicionResponseDTO buscarPorId(Long id) {
        logger.info("Buscando solicitud de reposición con ID: " + id);

        SolicitudReposicion solicitud = obtenerSolicitud(id);

        return convertirAResponse(solicitud);
    }

    public SolicitudReposicionResponseDTO guardar(
            SolicitudReposicionRequestDTO dto) {

        logger.info("Creando solicitud de reposición para producto ID: "
                + dto.getProductoId());

        Proveedor proveedor = proveedorService.obtenerProveedorEntidad(dto.getProveedorId());

        ProductoInventarioDTO producto = obtenerProductoInventario(dto.getProductoId());

        SolicitudReposicion solicitud = new SolicitudReposicion();

        solicitud.setProveedorId(proveedor.getId());
        solicitud.setNombreProveedor(proveedor.getNombre());
        solicitud.setProductoId(producto.getId());
        solicitud.setNombreProducto(producto.getNombre());
        solicitud.setCantidad(dto.getCantidad());
        solicitud.setEstado(normalizarEstado(dto.getEstado()));

        SolicitudReposicion solicitudGuardada = repository.save(solicitud);

        logger.info("Solicitud de reposición creada con ID: "
                + solicitudGuardada.getId());

        return convertirAResponse(solicitudGuardada);
    }

    public SolicitudReposicionResponseDTO actualizar(
            Long id,
            SolicitudReposicionRequestDTO dto) {

        logger.info("Actualizando solicitud de reposición con ID: " + id);

        SolicitudReposicion solicitud = obtenerSolicitud(id);

        Proveedor proveedor = proveedorService.obtenerProveedorEntidad(dto.getProveedorId());

        ProductoInventarioDTO producto = obtenerProductoInventario(dto.getProductoId());

        solicitud.setProveedorId(proveedor.getId());
        solicitud.setNombreProveedor(proveedor.getNombre());
        solicitud.setProductoId(producto.getId());
        solicitud.setNombreProducto(producto.getNombre());
        solicitud.setCantidad(dto.getCantidad());
        solicitud.setEstado(normalizarEstado(dto.getEstado()));

        SolicitudReposicion solicitudActualizada = repository.save(solicitud);

        logger.info("Solicitud de reposición actualizada con ID: "
                + solicitudActualizada.getId());

        return convertirAResponse(solicitudActualizada);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando solicitud de reposición con ID: " + id);

        SolicitudReposicion solicitud = obtenerSolicitud(id);

        repository.delete(solicitud);

        logger.info("Solicitud de reposición eliminada con ID: " + id);
    }

    private SolicitudReposicion obtenerSolicitud(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Solicitud de reposición no encontrada con ID: " + id);

                    return new SolicitudReposicionNoEncontradaException(
                            "Solicitud de reposición no encontrada");
                });
    }

    private ProductoInventarioDTO obtenerProductoInventario(Long id) {
        try {
            logger.info("Validando producto de inventario ID: " + id);

            return webClient.get()
                    .uri("http://localhost:8087/productos/" + id + "/dto")
                    .retrieve()
                    .bodyToMono(ProductoInventarioDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            logger.error("Producto de inventario no encontrado con ID: " + id);

            throw new ProductoInventarioNoEncontradoException(
                    "El producto de inventario con ID " + id + " no existe");

        } catch (Exception e) {
            logger.error("Error al comunicarse con ms-inventario");

            throw new ErrorComunicacionException(
                    "No se pudo validar el producto. Verifica que ms-inventario esté funcionando");
        }
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return "PENDIENTE";
        }

        String estadoNormalizado = estado.toUpperCase();

        if (!estadoNormalizado.equals("PENDIENTE")
                && !estadoNormalizado.equals("APROBADA")
                && !estadoNormalizado.equals("RECIBIDA")
                && !estadoNormalizado.equals("CANCELADA")) {

            throw new ReglaNegocioException(
                    "El estado debe ser PENDIENTE, APROBADA, RECIBIDA o CANCELADA");
        }

        return estadoNormalizado;
    }

    private SolicitudReposicionResponseDTO convertirAResponse(
            SolicitudReposicion solicitud) {

        return new SolicitudReposicionResponseDTO(
                solicitud.getId(),
                solicitud.getProveedorId(),
                solicitud.getNombreProveedor(),
                solicitud.getProductoId(),
                solicitud.getNombreProducto(),
                solicitud.getCantidad(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud());
    }
}