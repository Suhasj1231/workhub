package com.smj.workhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "WorkHub API",
                description = "WorkHub backend APIs for managing workspaces",
                version = "v1"
        )
)
public class OpenApiConfig {
}

