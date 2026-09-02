package cl.bahatech.pinball.domain.model;

import cl.bahatech.pinball.domain.exception.InvalidPinballMachineConditionRatingException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineFunctionalStatusException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineManufacturerException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineModelNameException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineReleaseYearException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineRestorationCostException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineUnitsProducedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestPinballMachine {

    private PinballMachine pinballMachine;

    @BeforeEach
    void setUp() {
        pinballMachine = new PinballMachine();
    }

    @Test
    void shouldCreatePinballMachineWithConstructor() {

        PinballMachine machine = new PinballMachine(
                1L, "Haunted Madness", "Spooky Pinball", "De Coleccion", "https://test.com/img.svg",
                "Resumen historico", 2018, 1200, new BigDecimal("1120.50"), 4.2, true, true);

        assertAll(
                () -> assertEquals(1L, machine.getId()),
                () -> assertEquals("Haunted Madness", machine.getModelName()),
                () -> assertEquals("Spooky Pinball", machine.getManufacturer()),
                () -> assertEquals(2018, machine.getReleaseYear()),
                () -> assertEquals(1200, machine.getUnitsProduced()),
                () -> assertEquals(new BigDecimal("1120.50"), machine.getRestorationCostUsd()),
                () -> assertEquals(4.2, machine.getConditionRating()),
                () -> assertEquals(true, machine.getIsFullyFunctional()),
                () -> assertEquals(true, machine.getHasMultiball())
        );
    }

    @Test
    void shouldCreatePinballMachineWithEmptyConstructorAndSetters() {

        pinballMachine.setId(2L);
        pinballMachine.setModelName("Space Cadet");
        pinballMachine.setManufacturer("Maxis");
        pinballMachine.setRarityTier("Comun");
        pinballMachine.setImageUrl("https://test.com/space.svg");
        pinballMachine.setHistoricalSummary("Resumen");
        pinballMachine.setReleaseYear(1995);
        pinballMachine.setUnitsProduced(300);
        pinballMachine.setRestorationCostUsd(new BigDecimal("50.00"));
        pinballMachine.setConditionRating(3.0);
        pinballMachine.setIsFullyFunctional(false);
        pinballMachine.setHasMultiball(false);

        assertAll(
                () -> assertEquals(2L, pinballMachine.getId()),
                () -> assertEquals("Space Cadet", pinballMachine.getModelName()),
                () -> assertEquals("Maxis", pinballMachine.getManufacturer()),
                () -> assertEquals("Comun", pinballMachine.getRarityTier()),
                () -> assertEquals("https://test.com/space.svg", pinballMachine.getImageUrl()),
                () -> assertEquals("Resumen", pinballMachine.getHistoricalSummary()),
                () -> assertEquals(1995, pinballMachine.getReleaseYear()),
                () -> assertEquals(300, pinballMachine.getUnitsProduced()),
                () -> assertEquals(new BigDecimal("50.00"), pinballMachine.getRestorationCostUsd()),
                () -> assertEquals(3.0, pinballMachine.getConditionRating()),
                () -> assertEquals(false, pinballMachine.getIsFullyFunctional()),
                () -> assertEquals(false, pinballMachine.getHasMultiball())
        );
    }

    @Test
    void shouldTrimModelNameAndManufacturer() {

        pinballMachine.setModelName("  Twilight Zone  ");
        pinballMachine.setManufacturer("  Bally  ");

        assertAll(
                () -> assertEquals("Twilight Zone", pinballMachine.getModelName()),
                () -> assertEquals("Bally", pinballMachine.getManufacturer())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "A"})
    void shouldThrowInvalidPinballMachineModelNameExceptionForInvalidValues(String invalidModelName) {

        assertThrows(
                InvalidPinballMachineModelNameException.class,
                () -> pinballMachine.validateModelName(invalidModelName));
    }

    @Test
    void shouldThrowInvalidPinballMachineModelNameExceptionWhenTooLong() {

        String tooLong = "a".repeat(121);

        assertThrows(
                InvalidPinballMachineModelNameException.class,
                () -> pinballMachine.validateModelName(tooLong));
    }

    @ParameterizedTest
    @ValueSource(strings = {"AB", "Twilight Zone", "Neon Rush 2077"})
    void shouldValidateModelNameSuccessfully(String validModelName) {

        assertDoesNotThrow(() -> pinballMachine.validateModelName(validModelName));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "B"})
    void shouldThrowInvalidPinballMachineManufacturerExceptionForInvalidValues(String invalidManufacturer) {

        assertThrows(
                InvalidPinballMachineManufacturerException.class,
                () -> pinballMachine.validateManufacturer(invalidManufacturer));
    }

    @Test
    void shouldThrowInvalidPinballMachineManufacturerExceptionWhenTooLong() {

        String tooLong = "b".repeat(81);

        assertThrows(
                InvalidPinballMachineManufacturerException.class,
                () -> pinballMachine.validateManufacturer(tooLong));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bally", "Williams", "Cyber Arcade Co."})
    void shouldValidateManufacturerSuccessfully(String validManufacturer) {

        assertDoesNotThrow(() -> pinballMachine.validateManufacturer(validManufacturer));
    }

    @Test
    void shouldThrowInvalidPinballMachineReleaseYearExceptionWhenNull() {

        assertThrows(
                InvalidPinballMachineReleaseYearException.class,
                () -> pinballMachine.validateReleaseYear(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {1800, 1929, 2101, 3000})
    void shouldThrowInvalidPinballMachineReleaseYearExceptionWhenOutOfRange(int invalidYear) {

        assertThrows(
                InvalidPinballMachineReleaseYearException.class,
                () -> pinballMachine.validateReleaseYear(invalidYear));
    }

    @ParameterizedTest
    @ValueSource(ints = {1930, 1993, 2026, 2100})
    void shouldValidateReleaseYearSuccessfully(int validYear) {

        assertDoesNotThrow(() -> pinballMachine.validateReleaseYear(validYear));
    }

    @Test
    void shouldAllowNullUnitsProduced() {

        assertDoesNotThrow(() -> pinballMachine.validateUnitsProduced(null));
    }

    @Test
    void shouldThrowInvalidPinballMachineUnitsProducedExceptionWhenNegative() {

        assertThrows(
                InvalidPinballMachineUnitsProducedException.class,
                () -> pinballMachine.validateUnitsProduced(-1));
    }

    @Test
    void shouldValidateUnitsProducedSuccessfully() {

        assertDoesNotThrow(() -> pinballMachine.validateUnitsProduced(0));
    }

    @Test
    void shouldAllowNullRestorationCost() {

        assertDoesNotThrow(() -> pinballMachine.validateRestorationCost(null));
    }

    @Test
    void shouldThrowInvalidPinballMachineRestorationCostExceptionWhenNegative() {

        assertThrows(
                InvalidPinballMachineRestorationCostException.class,
                () -> pinballMachine.validateRestorationCost(new BigDecimal("-0.01")));
    }

    @Test
    void shouldValidateRestorationCostSuccessfully() {

        assertDoesNotThrow(() -> pinballMachine.validateRestorationCost(BigDecimal.ZERO));
    }

    @Test
    void shouldAllowNullConditionRating() {

        assertDoesNotThrow(() -> pinballMachine.validateConditionRating(null));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.9, 5.1, 10.0})
    void shouldThrowInvalidPinballMachineConditionRatingExceptionWhenOutOfRange(double invalidRating) {

        assertThrows(
                InvalidPinballMachineConditionRatingException.class,
                () -> pinballMachine.validateConditionRating(invalidRating));
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 3.5, 5.0})
    void shouldValidateConditionRatingSuccessfully(double validRating) {

        assertDoesNotThrow(() -> pinballMachine.validateConditionRating(validRating));
    }

    @Test
    void shouldThrowInvalidPinballMachineFunctionalStatusExceptionWhenIsFullyFunctionalIsNull() {

        InvalidPinballMachineFunctionalStatusException ex = assertThrows(
                InvalidPinballMachineFunctionalStatusException.class,
                () -> pinballMachine.validateIsFullyFunctional(null));

        assertEquals("isFullyFunctional must be provided", ex.getMessage());
    }

    @Test
    void shouldThrowInvalidPinballMachineFunctionalStatusExceptionWhenHasMultiballIsNull() {

        InvalidPinballMachineFunctionalStatusException ex = assertThrows(
                InvalidPinballMachineFunctionalStatusException.class,
                () -> pinballMachine.validateHasMultiball(null));

        assertEquals("hasMultiball must be provided", ex.getMessage());
    }

    @Test
    void shouldValidateFunctionalFlagsSuccessfully() {

        assertAll(
                () -> assertDoesNotThrow(() -> pinballMachine.validateIsFullyFunctional(true)),
                () -> assertDoesNotThrow(() -> pinballMachine.validateIsFullyFunctional(false)),
                () -> assertDoesNotThrow(() -> pinballMachine.validateHasMultiball(true)),
                () -> assertDoesNotThrow(() -> pinballMachine.validateHasMultiball(false))
        );
    }

    @Test
    void shouldThrowInvalidPinballMachineModelNameExceptionWhenConstructorReceivesInvalidModelName() {

        assertThrows(
                InvalidPinballMachineModelNameException.class,
                () -> new PinballMachine(null, "A", "Bally", null, null, null,
                        1993, null, null, null, true, true));
    }

    @Test
    void shouldThrowInvalidPinballMachineManufacturerExceptionWhenSetterReceivesInvalidValue() {

        assertThrows(
                InvalidPinballMachineManufacturerException.class,
                () -> pinballMachine.setManufacturer(""));
    }

    @Test
    void shouldThrowInvalidPinballMachineReleaseYearExceptionWhenSetterReceivesInvalidValue() {

        assertThrows(
                InvalidPinballMachineReleaseYearException.class,
                () -> pinballMachine.setReleaseYear(1800));
    }

    @Test
    void shouldThrowInvalidPinballMachineUnitsProducedExceptionWhenSetterReceivesInvalidValue() {

        assertThrows(
                InvalidPinballMachineUnitsProducedException.class,
                () -> pinballMachine.setUnitsProduced(-5));
    }

    @Test
    void shouldThrowInvalidPinballMachineRestorationCostExceptionWhenSetterReceivesInvalidValue() {

        assertThrows(
                InvalidPinballMachineRestorationCostException.class,
                () -> pinballMachine.setRestorationCostUsd(new BigDecimal("-10")));
    }

    @Test
    void shouldThrowInvalidPinballMachineConditionRatingExceptionWhenSetterReceivesInvalidValue() {

        assertThrows(
                InvalidPinballMachineConditionRatingException.class,
                () -> pinballMachine.setConditionRating(6.0));
    }

    @Test
    void shouldThrowInvalidPinballMachineFunctionalStatusExceptionWhenIsFullyFunctionalSetterReceivesNull() {

        assertThrows(
                InvalidPinballMachineFunctionalStatusException.class,
                () -> pinballMachine.setIsFullyFunctional(null));
    }

    @Test
    void shouldThrowInvalidPinballMachineFunctionalStatusExceptionWhenHasMultiballSetterReceivesNull() {

        assertThrows(
                InvalidPinballMachineFunctionalStatusException.class,
                () -> pinballMachine.setHasMultiball(null));
    }

    @Test
    void shouldSetOptionalDescriptiveFieldsWithoutValidation() {

        pinballMachine.setRarityTier(null);
        pinballMachine.setImageUrl(null);
        pinballMachine.setHistoricalSummary(null);

        assertAll(
                () -> assertEquals(null, pinballMachine.getRarityTier()),
                () -> assertEquals(null, pinballMachine.getImageUrl()),
                () -> assertEquals(null, pinballMachine.getHistoricalSummary())
        );
    }
}
