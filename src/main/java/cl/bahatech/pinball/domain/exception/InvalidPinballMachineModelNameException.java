package cl.bahatech.pinball.domain.exception;

public class InvalidPinballMachineModelNameException extends RuntimeException {
    public InvalidPinballMachineModelNameException(String message) {
        super(message);
    }
}
