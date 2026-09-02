package cl.bahatech.pinball.infrastructure.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestOpenApiConfig {

    @Test
    void shouldCreateOpenApiBeanWithExpectedMetadata() {

        OpenApiConfig config = new OpenApiConfig();

        OpenAPI openApi = config.customOpenAPI();

        assertAll(
                () -> assertNotNull(openApi),
                () -> assertNotNull(openApi.getInfo()),
                () -> assertEquals("Arcademania API - Pinball", openApi.getInfo().getTitle()),
                () -> assertEquals("1.0", openApi.getInfo().getVersion()),
                () -> assertNotNull(openApi.getInfo().getContact()),
                () -> assertEquals("Bahatech", openApi.getInfo().getContact().getName()),
                () -> assertNotNull(openApi.getInfo().getLicense()),
                () -> assertEquals("MIT License", openApi.getInfo().getLicense().getName())
        );
    }
}
