package com.nexushub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nexusHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NexusHub API")
                        .version("1.0.0")
                        .description("REST API for NexusHub — a simple marketplace platform")
                        .contact(new Contact()
                                .name("NexusHub Team")
                                .email("contact@nexushub.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server")
                ));
    }
}
