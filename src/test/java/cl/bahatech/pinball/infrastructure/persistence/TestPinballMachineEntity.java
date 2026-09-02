package cl.bahatech.pinball.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPinballMachineEntity {

    private PinballMachineEntity entity;

    @BeforeEach
    void setUp() {
        entity = new PinballMachineEntity(
                "Radical Flippers", "Bally / Midway", "Clasica", "https://test.com/rf.svg",
                "Resumen", 1988, 6500, new BigDecimal("980.00"), 3.9, true, false);
        entity.setId(1L);
    }

    @Test
    void shouldCreateEntityWithAllArgsConstructor() {

        assertAll(
                () -> assertEquals("Radical Flippers", entity.getModelName()),
                () -> assertEquals("Bally / Midway", entity.getManufacturer()),
                () -> assertEquals("Clasica", entity.getRarityTier()),
                () -> assertEquals("https://test.com/rf.svg", entity.getImageUrl()),
                () -> assertEquals("Resumen", entity.getHistoricalSummary()),
                () -> assertEquals(1988, entity.getReleaseYear()),
                () -> assertEquals(6500, entity.getUnitsProduced()),
                () -> assertEquals(new BigDecimal("980.00"), entity.getRestorationCostUsd()),
                () -> assertEquals(3.9, entity.getConditionRating()),
                () -> assertTrue(entity.getIsFullyFunctional()),
                () -> assertFalse(entity.getHasMultiball())
        );
    }

    @Test
    void shouldCreateEntityWithNoArgsConstructorAndSetters() {

        PinballMachineEntity empty = new PinballMachineEntity();
        empty.setId(2L);
        empty.setModelName("Space Cadet");
        empty.setManufacturer("Maxis");
        empty.setRarityTier("Comun");
        empty.setImageUrl("https://test.com/sc.svg");
        empty.setHistoricalSummary("Otro resumen");
        empty.setReleaseYear(1995);
        empty.setUnitsProduced(300);
        empty.setRestorationCostUsd(new BigDecimal("50.00"));
        empty.setConditionRating(3.0);
        empty.setIsFullyFunctional(false);
        empty.setHasMultiball(false);

        assertAll(
                () -> assertEquals(2L, empty.getId()),
                () -> assertEquals("Space Cadet", empty.getModelName()),
                () -> assertEquals("Maxis", empty.getManufacturer())
        );
    }

    @Test
    void shouldBeEqualToItself() {

        assertEquals(entity, entity);
    }

    @Test
    void shouldBeEqualWhenSameId() {

        PinballMachineEntity other = new PinballMachineEntity();
        other.setId(1L);

        assertAll(
                () -> assertEquals(entity, other),
                () -> assertEquals(entity.hashCode(), other.hashCode())
        );
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {

        PinballMachineEntity other = new PinballMachineEntity();
        other.setId(2L);

        assertNotEquals(entity, other);
    }

    @Test
    void shouldNotBeEqualToNull() {

        // El orden importa: assertNotEquals(a, b) llama a a.equals(b).
        // entity debe ir primero para que se ejecute NUESTRO equals() con o=null,
        // y no el comportamiento por defecto al pasar null como primer argumento.
        assertNotEquals(entity, null);
    }

    @Test
    void shouldNotBeEqualToDifferentClass() {

        // Mismo motivo: entity primero, para que se invoque entity.equals("...")
        // en vez de "...".equals(entity) (que ejecutaria el equals() de String).
        assertNotEquals(entity, "not-an-entity");
    }

    @Test
    void shouldBeEqualWhenBothIdsAreNull() {

        PinballMachineEntity withoutId = new PinballMachineEntity();
        PinballMachineEntity otherWithoutId = new PinballMachineEntity();

        assertAll(
                () -> assertEquals(withoutId, otherWithoutId),
                () -> assertEquals(withoutId.hashCode(), otherWithoutId.hashCode())
        );
    }

    @Test
    void shouldNotBeEqualWhenOnlyThisIdIsNull() {

        PinballMachineEntity withoutId = new PinballMachineEntity();

        assertNotEquals(withoutId, entity);
    }

    @Test
    void shouldNotBeEqualWhenOnlyOtherIdIsNull() {

        PinballMachineEntity withoutId = new PinballMachineEntity();

        assertNotEquals(entity, withoutId);
    }
}
