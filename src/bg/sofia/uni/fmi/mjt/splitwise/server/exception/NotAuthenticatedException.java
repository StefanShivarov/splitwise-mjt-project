package bg.sofia.uni.fmi.mjt.splitwise.server.exception;

public class NotAuthenticatedException extends AuthenticationException {

    public NotAuthenticatedException() {
    }

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
