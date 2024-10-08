package com.yoobee.licenseplate.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger2
{


    @Bean
    public OpenAPI restfulOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("car system")
                        .description("Spring Boot3 Restful API")
                        .version("V1.0.0")
                        .license(new License().name("Apache License").url("https://github.com/chenjunbo")))
                .externalDocs(new ExternalDocumentation()
                        .description("Welcome to car system")
                        .url("https://github.com/chenjunbo"));
    }

}
