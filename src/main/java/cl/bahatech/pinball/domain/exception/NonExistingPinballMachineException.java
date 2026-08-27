package cl.bahatech.pinball.domain.exception;

public class NonExistingPinballMachineException extends RuntimeException {

    public NonExistingPinballMachineException(String message) {
        super(message);
    }

}
