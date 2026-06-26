package com.raizesnordeste.infrastructure.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Raízes do Nordeste — API Back-End")
                .description("API REST da rede de lanchonetes Raízes do Nordeste. " +
                             "Suporta múltiplos canais: APP, TOTEM, BALCÃO, PICKUP e WEB. " +
                             "Desenvolvido por Matheus Bessado (RU: 4712789) - UNINTER 2026.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Matheus Bessado — RU: 4712789")
                    .email("4712789@uninter.edu")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Informe o token JWT obtido em POST /auth/login")));
    }
}
