package bg.sofia.uni.fmi.mjt.splitwise.server.exception;

public class InvalidCommandInputException extends Exception {

    public InvalidCommandInputException(String message) {
        super(message);
    }

    public InvalidCommandInputException(String message, Throwable cause) {
        super(message, cause);
    }

}
