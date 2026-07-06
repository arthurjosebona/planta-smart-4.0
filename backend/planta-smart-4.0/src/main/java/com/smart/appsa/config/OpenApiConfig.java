package com.smart.appsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuração da documentação OpenAPI (Swagger UI).
 *
 * Após subir a aplicação, a documentação interativa fica disponível em:
 *   - Swagger UI:   http://localhost:8080/swagger-ui.html
 *   - JSON OpenAPI: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI plantaSmartOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Planta Smart 4.0 — API")
                        .description("API REST de supervisão e controle da Bancada Smart 4.0. "
                                + "Gerencia pedidos, estoque, expedição, configuração dos CLPs e "
                                + "a comunicação em tempo real (S7 / SSE) com as estações da linha de produção.")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Arthur José Bona")
                                .url("https://github.com/arthurjosebona"))
                        .license(new License().name("SENAI — Situação de Aprendizagem")));
    }
}
