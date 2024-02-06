package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.io.PrintWriter;

public class LogoutCommand implements Command {

    private final AuthenticationManager authManager;

    public LogoutCommand(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        authManager.logout();
        out.println("Logout successful!" + System.lineSeparator() +
                "Please register or login to get started.");
    }

}
