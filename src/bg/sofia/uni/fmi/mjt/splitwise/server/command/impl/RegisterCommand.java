package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.PrintWriter;

public class RegisterCommand implements Command {

    private final AuthenticationManager authManager;
    private final UserService userService;

    public RegisterCommand(AuthenticationManager authManager, UserService userService) {
        this.authManager = authManager;
        this.userService = userService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, AlreadyAuthenticatedException {
        validate();

        switch (inputTokens.length) {
            case 3 -> userService.addUser(inputTokens[1], inputTokens[2], "", "");
            case 4 -> userService.addUser(inputTokens[1], inputTokens[2], inputTokens[3], "");
            case 5 -> userService.addUser(inputTokens[1], inputTokens[2], inputTokens[3], inputTokens[4]);
            default -> throw new InvalidCommandInputException(
                    "Invalid user information! User can't be created!");
        }
    }

    private void validate() throws AlreadyAuthenticatedException {
        if (authManager.isAuthenticated()) {
            throw new AlreadyAuthenticatedException(
                    "You don't have access to this command! You are already logged in.");
        }
    }

}
