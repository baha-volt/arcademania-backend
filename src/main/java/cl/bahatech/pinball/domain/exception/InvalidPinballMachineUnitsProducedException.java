package cl.bahatech.pinball.domain.exception;

public class InvalidPinballMachineUnitsProducedException extends RuntimeException {
    public InvalidPinballMachineUnitsProducedException(String message) {
        super(message);
    }
}
