package cl.bahatech.pinball.domain.model;

import java.math.BigDecimal;

public record PinballMachine(
        Long id,
        String modelName,
        String manufacturer,
        String rarityTier,
        String imageUrl,
        String historicalSummary,
        Integer releaseYear,
        Integer unitsProduced,
        BigDecimal restorationCostUsd,
        Double conditionRating,
        Boolean isFullyFunctional,
        Boolean hasMultiball
) {
}
