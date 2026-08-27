package cl.bahatech.pinball.infrastructure.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Arcademania API - Pinball")
                .version("1.0")
                .description("Documentacion de la API REST para el catalogo y conservacion de maquinas de pinball vintage")
                .contact(new Contact()
                    .name("Bahatech"))
                .license(new License()
                    .name("MIT License")));
    }

}
