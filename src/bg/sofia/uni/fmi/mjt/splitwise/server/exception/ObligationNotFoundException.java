package bg.sofia.uni.fmi.mjt.splitwise.server.exception;

public class ObligationNotFoundException extends Exception {

    public ObligationNotFoundException(String message) {
        super(message);
    }

    public ObligationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
