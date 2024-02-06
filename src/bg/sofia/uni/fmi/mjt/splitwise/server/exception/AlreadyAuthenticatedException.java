package bg.sofia.uni.fmi.mjt.splitwise.server.exception;

public class AlreadyAuthenticatedException extends AuthenticationException {

    public AlreadyAuthenticatedException(String message) {
        super(message);
    }

    public AlreadyAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
