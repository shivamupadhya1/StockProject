package com.stock.in.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Stock Tracker API", version = "1.0", description = "API for tracking stock prices"),
    servers = {
        @Server(url = "http://localhost:8080/stock-tracker", description = "Local Development Server"),
        @Server(url = "https://uat.example.com/stock-tracker", description = "UAT Server")
    }
)
public class SwaggerConfig {
}
