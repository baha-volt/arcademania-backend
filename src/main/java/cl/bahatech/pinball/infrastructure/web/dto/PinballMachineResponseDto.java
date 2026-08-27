package cl.bahatech.pinball.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Representacion de una maquina de pinball del catalogo")
public record PinballMachineResponseDto(

    @Schema(description = "Identificador numerico de la maquina", example = "1")
    Long id,

    @Schema(description = "Nombre del modelo de la maquina", example = "Medieval Madness")
    String modelName,

    @Schema(description = "Fabricante de la maquina", example = "Williams")
    String manufacturer,

    @Schema(description = "Nivel de rareza de la maquina", example = "Edicion Limitada")
    String rarityTier,

    @Schema(description = "URL de la imagen representativa de la maquina", example = "https://example.com/medieval-madness.jpg")
    String imageUrl,

    @Schema(description = "Resumen historico de la maquina")
    String historicalSummary,

    @Schema(description = "Ano de lanzamiento de la maquina", example = "1997")
    Integer releaseYear,

    @Schema(description = "Cantidad de unidades producidas", example = "4016")
    Integer unitsProduced,

    @Schema(description = "Costo estimado de restauracion en dolares", example = "1200.50")
    BigDecimal restorationCostUsd,

    @Schema(description = "Puntuacion del estado de conservacion, de 1.0 a 5.0", example = "4.5")
    Double conditionRating,

    @Schema(description = "Indica si la maquina esta completamente funcional", example = "true")
    Boolean isFullyFunctional,

    @Schema(description = "Indica si la maquina cuenta con modo multiball", example = "true")
    Boolean hasMultiball

) {
}
