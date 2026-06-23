package com.example.car_shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car Shop API")
                        .version("1.0.0")
                        .description("Простой учебный СRUD")
                        .contact(new Contact()
                                .name("Mendeleev258")
                                .email("jroslavl.2005@gmail.com")
                                .url("https://github.com/Mendeleev258")));
    }
}