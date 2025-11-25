package com.upiiz.pvz_chess_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Plantas vs Zombies Chess API",
                version = "1.0.0",
                description = "API backend para el juego multijugador Plantas vs Zombies Chess.\n\n" +
                        "Incluye registro de jugadores, gestión de tokens FCM y envío de notificaciones de reto.",
                contact = @Contact(
                        name = "Aly",
                        email = "aly@example.com"
                ),
                license = @License(
                        name = "Uso académico",
                        url = "https://example.com"
                )
        ),
        servers = {
                @Server(
                        description = "Servidor local",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Servidor Render",
                        url = "https://pvz-chess-api-aly.onrender.com"
                )
        }
)
public class OpenApiConfig {

}
