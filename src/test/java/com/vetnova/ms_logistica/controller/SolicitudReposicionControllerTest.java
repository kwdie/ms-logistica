package com.vetnova.ms_logistica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vetnova.ms_logistica.dto.SolicitudReposicionRequestDTO;
import com.vetnova.ms_logistica.dto.SolicitudReposicionResponseDTO;
import com.vetnova.ms_logistica.exception.GlobalExceptionHandler;
import com.vetnova.ms_logistica.exception.SolicitudReposicionNoEncontradaException;
import com.vetnova.ms_logistica.service.SolicitudReposicionService;

@ExtendWith(MockitoExtension.class)
class SolicitudReposicionControllerTest {

    @Mock
    private SolicitudReposicionService solicitudService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SolicitudReposicionController controller = new SolicitudReposicionController(solicitudService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    @DisplayName("GET /solicitudes-reposicion debe listar solicitudes")
    void debeListarSolicitudes() throws Exception {
        SolicitudReposicionResponseDTO solicitud1 = new SolicitudReposicionResponseDTO(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE",
                null);

        SolicitudReposicionResponseDTO solicitud2 = new SolicitudReposicionResponseDTO(
                2L,
                2L,
                "María López",
                11L,
                "Shampoo Medicado",
                3,
                "APROBADA",
                null);

        when(solicitudService.listar()).thenReturn(List.of(solicitud1, solicitud2));

        mockMvc.perform(get("/solicitudes-reposicion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].proveedor").value("Carlos Pérez"))
                .andExpect(jsonPath("$[0].producto").value("Alimento Premium"))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].proveedor").value("María López"));

        verify(solicitudService).listar();
    }

    @Test
    @DisplayName("POST /solicitudes-reposicion debe guardar solicitud")
    void debeGuardarSolicitud() throws Exception {
        SolicitudReposicionResponseDTO respuesta = new SolicitudReposicionResponseDTO(
                1L,
                1L,
                "Carlos Pérez",
                10L,
                "Alimento Premium",
                5,
                "PENDIENTE",
                null);

        when(solicitudService.guardar(any(SolicitudReposicionRequestDTO.class)))
                .thenReturn(respuesta);

        String json = """
                {
                    "proveedorId": 1,
                    "productoId": 10,
                    "cantidad": 5,
                    "estado": "PENDIENTE"
                }
                """;

        mockMvc.perform(post("/solicitudes-reposicion")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.proveedor").value("Carlos Pérez"))
                .andExpect(jsonPath("$.producto").value("Alimento Premium"))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(solicitudService).guardar(any(SolicitudReposicionRequestDTO.class));
    }

    @Test
    @DisplayName("GET /solicitudes-reposicion/{id} debe retornar 404 si no existe")
    void debeRetornar404SiSolicitudNoExiste() throws Exception {
        when(solicitudService.buscarPorId(99L))
                .thenThrow(new SolicitudReposicionNoEncontradaException("Solicitud no encontrada"));

        mockMvc.perform(get("/solicitudes-reposicion/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Solicitud no encontrada\""));

        verify(solicitudService).buscarPorId(99L);
    }
}