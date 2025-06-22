package com.microserviceone.users.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Registro de Usuarios")
                .version("1.0")
                .description("API para el registro y gestión de usuarios")
                .contact(new Contact()
                    .name("MicroserviceOne")
                    .email("contacto@microserviceone.com")
                    .url("https://microserviceone.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}