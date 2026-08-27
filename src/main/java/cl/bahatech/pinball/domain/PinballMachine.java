package cl.bahatech.pinball.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "pinball_machines", schema = "pinball")
public class PinballMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pinball")
    private Long id;

    @Column(name = "model_name", nullable = false, length = 120)
    private String modelName;

    @Column(name = "manufacturer", nullable = false, length = 80)
    private String manufacturer;

    @Column(name = "rarity_tier", length = 30)
    private String rarityTier;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "historical_summary", columnDefinition = "TEXT")
    private String historicalSummary;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @Column(name = "units_produced")
    private Integer unitsProduced;

    @Column(name = "restoration_cost_usd", precision = 10, scale = 2)
    private BigDecimal restorationCostUsd;

    @Column(name = "condition_rating")
    private Double conditionRating;

    @Column(name = "is_fully_functional", nullable = false)
    private Boolean isFullyFunctional;

    @Column(name = "has_multiball", nullable = false)
    private Boolean hasMultiball;

    public PinballMachine() {
    }

    public PinballMachine(String modelName, String manufacturer, String rarityTier,
                           String imageUrl, String historicalSummary, Integer releaseYear,
                           Integer unitsProduced, BigDecimal restorationCostUsd,
                           Double conditionRating, Boolean isFullyFunctional, Boolean hasMultiball) {
        this.modelName = modelName;
        this.manufacturer = manufacturer;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getRarityTier() { return rarityTier; }
    public void setRarityTier(String rarityTier) { this.rarityTier = rarityTier; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getHistoricalSummary() { return historicalSummary; }
    public void setHistoricalSummary(String historicalSummary) { this.historicalSummary = historicalSummary; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public Integer getUnitsProduced() { return unitsProduced; }
    public void setUnitsProduced(Integer unitsProduced) { this.unitsProduced = unitsProduced; }

    public BigDecimal getRestorationCostUsd() { return restorationCostUsd; }
    public void setRestorationCostUsd(BigDecimal restorationCostUsd) { this.restorationCostUsd = restorationCostUsd; }

    public Double getConditionRating() { return conditionRating; }
    public void setConditionRating(Double conditionRating) { this.conditionRating = conditionRating; }

    public Boolean getIsFullyFunctional() { return isFullyFunctional; }
    public void setIsFullyFunctional(Boolean isFullyFunctional) { this.isFullyFunctional = isFullyFunctional; }

    public Boolean getHasMultiball() { return hasMultiball; }
    public void setHasMultiball(Boolean hasMultiball) { this.hasMultiball = hasMultiball; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PinballMachine that = (PinballMachine) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
