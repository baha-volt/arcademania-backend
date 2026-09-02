package cl.bahatech.pinball.domain.model;

import cl.bahatech.pinball.domain.exception.InvalidPinballMachineConditionRatingException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineFunctionalStatusException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineManufacturerException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineModelNameException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineReleaseYearException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineRestorationCostException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineUnitsProducedException;

import java.math.BigDecimal;

public class PinballMachine {

    private static final int MINIMUM_RELEASE_YEAR = 1930;
    private static final int MAXIMUM_RELEASE_YEAR = 2100;

    private Long id;
    private String modelName;
    private String manufacturer;
    private String rarityTier;
    private String imageUrl;
    private String historicalSummary;
    private Integer releaseYear;
    private Integer unitsProduced;
    private BigDecimal restorationCostUsd;
    private Double conditionRating;
    private Boolean isFullyFunctional;
    private Boolean hasMultiball;

    public PinballMachine() {
    }

    public PinballMachine(Long id, String modelName, String manufacturer, String rarityTier, String imageUrl,
                           String historicalSummary, Integer releaseYear, Integer unitsProduced,
                           BigDecimal restorationCostUsd, Double conditionRating,
                           Boolean isFullyFunctional, Boolean hasMultiball) {
        validateModelName(modelName);
        validateManufacturer(manufacturer);
        validateReleaseYear(releaseYear);
        validateUnitsProduced(unitsProduced);
        validateRestorationCost(restorationCostUsd);
        validateConditionRating(conditionRating);
        validateIsFullyFunctional(isFullyFunctional);
        validateHasMultiball(hasMultiball);

        this.id = id;
        this.modelName = modelName.trim();
        this.manufacturer = manufacturer.trim();
        this.rarityTier = rarityTier;
        this.imageUrl = imageUrl;
        this.historicalSummary = historicalSummary;
        this.releaseYear = releaseYear;
        this.unitsProduced = unitsProduced;
        this.restorationCostUsd = restorationCostUsd;
        this.conditionRating = conditionRating;
        this.isFullyFunctional = isFullyFunctional;
        this.hasMultiball = hasMultiball;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        validateModelName(modelName);
        this.modelName = modelName.trim();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        validateManufacturer(manufacturer);
        this.manufacturer = manufacturer.trim();
    }

    public String getRarityTier() {
        return rarityTier;
    }

    public void setRarityTier(String rarityTier) {
        this.rarityTier = rarityTier;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHistoricalSummary() {
        return historicalSummary;
    }

    public void setHistoricalSummary(String historicalSummary) {
        this.historicalSummary = historicalSummary;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        validateReleaseYear(releaseYear);
        this.releaseYear = releaseYear;
    }

    public Integer getUnitsProduced() {
        return unitsProduced;
    }

    public void setUnitsProduced(Integer unitsProduced) {
        validateUnitsProduced(unitsProduced);
        this.unitsProduced = unitsProduced;
    }

    public BigDecimal getRestorationCostUsd() {
        return restorationCostUsd;
    }

    public void setRestorationCostUsd(BigDecimal restorationCostUsd) {
        validateRestorationCost(restorationCostUsd);
        this.restorationCostUsd = restorationCostUsd;
    }

    public Double getConditionRating() {
        return conditionRating;
    }

    public void setConditionRating(Double conditionRating) {
        validateConditionRating(conditionRating);
        this.conditionRating = conditionRating;
    }

    public Boolean getIsFullyFunctional() {
        return isFullyFunctional;
    }

    public void setIsFullyFunctional(Boolean isFullyFunctional) {
        validateIsFullyFunctional(isFullyFunctional);
        this.isFullyFunctional = isFullyFunctional;
    }

    public Boolean getHasMultiball() {
        return hasMultiball;
    }

    public void setHasMultiball(Boolean hasMultiball) {
        validateHasMultiball(hasMultiball);
        this.hasMultiball = hasMultiball;
    }

    public void validateModelName(String modelName) {
        if (null == modelName || modelName.isBlank()) {
            throw new InvalidPinballMachineModelNameException("Invalid model name");
        }
        if (modelName.trim().length() < 2 || modelName.trim().length() > 120) {
            throw new InvalidPinballMachineModelNameException("Model name must contain between 2 and 120 characters");
        }
    }

    public void validateManufacturer(String manufacturer) {
        if (null == manufacturer || manufacturer.isBlank()) {
            throw new InvalidPinballMachineManufacturerException("Invalid manufacturer");
        }
        if (manufacturer.trim().length() < 2 || manufacturer.trim().length() > 80) {
            throw new InvalidPinballMachineManufacturerException("Manufacturer must contain between 2 and 80 characters");
        }
    }

    public void validateReleaseYear(Integer releaseYear) {
        if (null == releaseYear) {
            throw new InvalidPinballMachineReleaseYearException("Release year cannot be null");
        }
        if (releaseYear < MINIMUM_RELEASE_YEAR || releaseYear > MAXIMUM_RELEASE_YEAR) {
            throw new InvalidPinballMachineReleaseYearException(
                    "Release year must be between " + MINIMUM_RELEASE_YEAR + " and " + MAXIMUM_RELEASE_YEAR);
        }
    }

    public void validateUnitsProduced(Integer unitsProduced) {
        if (unitsProduced != null && unitsProduced < 0) {
            throw new InvalidPinballMachineUnitsProducedException("Units produced cannot be negative");
        }
    }

    public void validateRestorationCost(BigDecimal restorationCostUsd) {
        if (restorationCostUsd != null && restorationCostUsd.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPinballMachineRestorationCostException("Restoration cost cannot be negative");
        }
    }

    public void validateConditionRating(Double conditionRating) {
        if (conditionRating != null && (conditionRating < 1.0 || conditionRating > 5.0)) {
            throw new InvalidPinballMachineConditionRatingException("Condition rating must be between 1.0 and 5.0");
        }
    }

    public void validateIsFullyFunctional(Boolean isFullyFunctional) {
        if (null == isFullyFunctional) {
            throw new InvalidPinballMachineFunctionalStatusException("isFullyFunctional must be provided");
        }
    }

    public void validateHasMultiball(Boolean hasMultiball) {
        if (null == hasMultiball) {
            throw new InvalidPinballMachineFunctionalStatusException("hasMultiball must be provided");
        }
    }
}
