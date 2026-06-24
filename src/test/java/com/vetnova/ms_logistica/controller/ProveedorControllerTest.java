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

import com.vetnova.ms_logistica.dto.ProveedorRequestDTO;
import com.vetnova.ms_logistica.dto.ProveedorResponseDTO;
import com.vetnova.ms_logistica.exception.GlobalExceptionHandler;
import com.vetnova.ms_logistica.exception.ProveedorNoEncontradoException;
import com.vetnova.ms_logistica.service.ProveedorService;

@ExtendWith(MockitoExtension.class)
class ProveedorControllerTest {

    @Mock
    private ProveedorService proveedorService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProveedorController controller = new ProveedorController(proveedorService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    @DisplayName("GET /proveedores debe listar proveedores")
    void debeListarProveedores() throws Exception {
        ProveedorResponseDTO proveedor1 = new ProveedorResponseDTO(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        ProveedorResponseDTO proveedor2 = new ProveedorResponseDTO(
                2L,
                "María López",
                "Insumos Vet",
                "987654321",
                "ventas@insumosvet.cl"
        );

        when(proveedorService.listar()).thenReturn(List.of(proveedor1, proveedor2));

        mockMvc.perform(get("/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Carlos Pérez"))
                .andExpect(jsonPath("$[0].empresa").value("PetFood Chile"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("María López"));

        verify(proveedorService).listar();
    }

    @Test
    @DisplayName("POST /proveedores debe guardar proveedor")
    void debeGuardarProveedor() throws Exception {
        ProveedorResponseDTO respuesta = new ProveedorResponseDTO(
                1L,
                "Carlos Pérez",
                "PetFood Chile",
                "912345678",
                "contacto@petfood.cl"
        );

        when(proveedorService.guardar(any(ProveedorRequestDTO.class)))
                .thenReturn(respuesta);

        String json = """
                {
                    "nombre": "Carlos Pérez",
                    "empresa": "PetFood Chile",
                    "telefono": "912345678",
                    "correo": "contacto@petfood.cl"
                }
                """;

        mockMvc.perform(post("/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos Pérez"))
                .andExpect(jsonPath("$.empresa").value("PetFood Chile"))
                .andExpect(jsonPath("$.correo").value("contacto@petfood.cl"));

        verify(proveedorService).guardar(any(ProveedorRequestDTO.class));
    }

    @Test
    @DisplayName("GET /proveedores/{id} debe retornar 404 si no existe")
    void debeRetornar404SiProveedorNoExiste() throws Exception {
        when(proveedorService.buscarPorId(99L))
                .thenThrow(new ProveedorNoEncontradoException("Proveedor no encontrado"));

        mockMvc.perform(get("/proveedores/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Proveedor no encontrado\""));

        verify(proveedorService).buscarPorId(99L);
    }
}