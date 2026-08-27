package cl.bahatech.pinball.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Estructura estandar para respuestas de error de la API")
public record ErrorResponse(

    @Schema(description = "Codigo de estado HTTP", example = "404")
    int code,

    @Schema(description = "Mensaje descriptivo del error", example = "Pinball machine with ID 99 not found")
    String message,

    @Schema(description = "Fecha y hora en que ocurrio el error")
    LocalDateTime timestamp

) {
}
