package cl.bahatech.pinball.infrastructure.web.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCorsConfig {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", new String[]{"http://localhost:5173"});
    }

    @Test
    void shouldCreateCorsConfigurerBean() {

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertNotNull(configurer);
    }

    @Test
    void shouldRegisterCorsMappingsWithoutErrors() {

        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        CorsRegistry registry = new CorsRegistry();

        assertDoesNotThrow(() -> configurer.addCorsMappings(registry));
    }
}
