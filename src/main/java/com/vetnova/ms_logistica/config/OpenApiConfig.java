package com.vetnova.ms_logistica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logisticaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetNova - MS Logística")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar proveedores y solicitudes de reposición de productos."));
    }
}