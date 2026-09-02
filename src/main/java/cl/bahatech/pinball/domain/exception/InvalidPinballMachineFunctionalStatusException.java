package cl.bahatech.pinball.domain.exception;

public class InvalidPinballMachineFunctionalStatusException extends RuntimeException {
    public InvalidPinballMachineFunctionalStatusException(String message) {
        super(message);
    }
}
