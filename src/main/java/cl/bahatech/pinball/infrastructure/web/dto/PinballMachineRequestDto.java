package cl.bahatech.pinball.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar o actualizar una maquina de pinball")
public record PinballMachineRequestDto(

    @Schema(description = "Nombre del modelo de la maquina", example = "Medieval Madness")
    @NotBlank(message = "Model name is required")
    @Size(max = 120, message = "Model name must be at most 120 characters")
    String modelName,

    @Schema(description = "Fabricante de la maquina", example = "Williams")
    @NotBlank(message = "Manufacturer is required")
    @Size(max = 80, message = "Manufacturer must be at most 80 characters")
    String manufacturer,

    @Schema(description = "Nivel de rareza de la maquina", example = "Edicion Limitada")
    @Size(max = 30, message = "Rarity tier must be at most 30 characters")
    String rarityTier,

    @Schema(description = "URL de la imagen representativa de la maquina", example = "https://example.com/medieval-madness.jpg")
    @Size(max = 500, message = "Image URL must be at most 500 characters")
    String imageUrl,

    @Schema(description = "Resumen historico de la maquina", example = "Considerado uno de los mejores pinballs de los anos 90")
    String historicalSummary,

    @Schema(description = "Ano de lanzamiento de la maquina", example = "1997")
    @NotNull(message = "Release year is required")
    @Min(value = 1930, message = "Release year must be 1930 or later")
    Integer releaseYear,

    @Schema(description = "Cantidad de unidades producidas", example = "4016")
    @Min(value = 0, message = "Units produced cannot be negative")
    Integer unitsProduced,

    @Schema(description = "Costo estimado de restauracion en dolares", example = "1200.50")
    @DecimalMin(value = "0.0", message = "Restoration cost cannot be negative")
    BigDecimal restorationCostUsd,

    @Schema(description = "Puntuacion del estado de conservacion, de 1.0 a 5.0", example = "4.5")
    @DecimalMin(value = "1.0", message = "Condition rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Condition rating must be at most 5.0")
    Double conditionRating,

    @Schema(description = "Indica si la maquina esta completamente funcional", example = "true")
    @NotNull(message = "isFullyFunctional is required")
    Boolean isFullyFunctional,

    @Schema(description = "Indica si la maquina cuenta con modo multiball", example = "true")
    @NotNull(message = "hasMultiball is required")
    Boolean hasMultiball

) {
}
