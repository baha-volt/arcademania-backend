package cl.bahatech.pinball.infrastructure.web.exception;

import cl.bahatech.pinball.domain.exception.DuplicatePinballMachineException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineConditionRatingException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineFunctionalStatusException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineManufacturerException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineModelNameException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineReleaseYearException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineRestorationCostException;
import cl.bahatech.pinball.domain.exception.InvalidPinballMachineUnitsProducedException;
import cl.bahatech.pinball.domain.exception.NonExistingPinballMachineException;
import cl.bahatech.pinball.infrastructure.web.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestGlobalExceptionHandler {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleNonExistingPinballMachineException() {

        NonExistingPinballMachineException ex = new NonExistingPinballMachineException("Pinball machine with ID 99 not found");

        ResponseEntity<ErrorResponse> response = handler.handleNonExisting(ex);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Pinball machine with ID 99 not found", response.getBody().message())
        );
    }

    @Test
    void shouldHandleDuplicatePinballMachineException() {

        DuplicatePinballMachineException ex = new DuplicatePinballMachineException("A pinball machine with model name 'X' already exists");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(ex);

        assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, response.getStatusCode()),
                () -> assertNotNull(response.getBody())
        );
    }

    @Test
    void shouldHandleInvalidPinballMachineModelNameException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineModelNameException("Invalid model name"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineManufacturerException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineManufacturerException("Invalid manufacturer"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineReleaseYearException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineReleaseYearException("Invalid release year"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineUnitsProducedException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineUnitsProducedException("Invalid units produced"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineRestorationCostException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineRestorationCostException("Invalid restoration cost"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineConditionRatingException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineConditionRatingException("Invalid condition rating"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleInvalidPinballMachineFunctionalStatusException() {

        ResponseEntity<ErrorResponse> response = handler.handleDomainValidation(
                new InvalidPinballMachineFunctionalStatusException("isFullyFunctional must be provided"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void shouldHandleGenericException() {

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"));

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Unexpected server error", response.getBody().message())
        );
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {

        FieldError fieldError = new FieldError("pinballMachineRequestDto", "modelName", "Model name is required");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("modelName: Model name is required", response.getBody().message())
        );
    }
}
