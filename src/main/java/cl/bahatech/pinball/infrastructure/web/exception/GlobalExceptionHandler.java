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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(NonExistingPinballMachineException.class)
    public ResponseEntity<ErrorResponse> handleNonExisting(NonExistingPinballMachineException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicatePinballMachineException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicatePinballMachineException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({
            InvalidPinballMachineModelNameException.class,
            InvalidPinballMachineManufacturerException.class,
            InvalidPinballMachineReleaseYearException.class,
            InvalidPinballMachineUnitsProducedException.class,
            InvalidPinballMachineRestorationCostException.class,
            InvalidPinballMachineConditionRatingException.class,
            InvalidPinballMachineFunctionalStatusException.class
    })
    public ResponseEntity<ErrorResponse> handleDomainValidation(RuntimeException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), message, LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }

}
