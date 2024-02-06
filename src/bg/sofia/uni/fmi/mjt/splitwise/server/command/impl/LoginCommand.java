package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;

import java.io.PrintWriter;
import java.util.Collection;

public class LoginCommand implements Command {

    private final AuthenticationManager authManager;
    private final NotificationService notificationService;
    private static final int USERNAME_INDEX = 1;
    private static final int PASS_INDEX = 2;
    private static final int MIN_TOKENS_AMOUNT = 3;

    public LoginCommand(AuthenticationManager authManager,
                        NotificationService notificationService) {
        this.authManager = authManager;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, AlreadyAuthenticatedException {
        validate(inputTokens);

        if (authManager.authenticate(inputTokens[USERNAME_INDEX], inputTokens[PASS_INDEX])) {
            try {
                Collection<Notification> notifications = notificationService
                        .getUnseenNotificationsForUser(
                                authManager.getAuthenticatedUser().getUsername());

                String notificationsOutput = notificationService
                        .getNotificationsOutput(notifications);

                notificationService.markNotificationsAsSeen(notifications);
                out.println("Welcome, " + authManager.getAuthenticatedUser().getUsername() + "!"
                        + System.lineSeparator() + notificationsOutput);
            } catch (UserNotFoundException e) {
                out.println(e.getMessage());
            }
        } else {
            out.println("Invalid credentials! Login unsuccessful!");
        }
    }

    private void validate(String[] inputTokens)
            throws AlreadyAuthenticatedException, InvalidCommandInputException {
        if (authManager.isAuthenticated()) {
            throw new AlreadyAuthenticatedException(
                    "You don't have access to this command! You are already logged in.");
        }

        if (inputTokens.length < MIN_TOKENS_AMOUNT) {
            throw new InvalidCommandInputException(
                    "Invalid command! Login must be login <username> <password>!");
        }
    }

}
