package com.ecommerce.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger
 *
 * This class sets up API documentation and enables JWT-based authentication
 * in Swagger UI using Bearer token.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates and configures the OpenAPI bean with JWT Bearer authentication
     * Configuration:
     * -defines a security scheme name "bearerAuth"
     * - Use HTTP Bearer authentication
     * - Specifies JWT as the token format
     * @return configured OpenApi instance with JWT security
     */
    @Bean
    public OpenAPI CustomOpenAPI()
    {
        return new OpenAPI()

        .components(new Components().
            addSecuritySchemes("bearerAuth",
                    new SecurityScheme().
                            type(SecurityScheme.Type.HTTP).
                            scheme("bearer").
                            bearerFormat("JWT")))

            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

    }

}
