package cl.bahatech.pinball.domain.exception;

public class InvalidPinballMachineReleaseYearException extends RuntimeException {
    public InvalidPinballMachineReleaseYearException(String message) {
        super(message);
    }
}
