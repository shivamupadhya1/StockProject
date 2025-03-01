package com.stock.in.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Tracker API")
                        .version("1.0")
                        .description("API for tracking Dow Jones Industrial Average stock prices")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
