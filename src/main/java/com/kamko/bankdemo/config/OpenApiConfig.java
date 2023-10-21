package com.kamko.bankdemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Aston test task",
                version = "1.0",
                contact = @Contact(
                        name = "Roman Kamko",
                        email = "r.kamko@mail.ru",
                        url = "https://t.me/Roman_Kamko"
                )
        )
)
public class OpenApiConfig {
}
