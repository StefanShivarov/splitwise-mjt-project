package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;

import java.io.PrintWriter;

public class AddFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final FriendshipService friendshipService;
    private final NotificationService notificationService;

    public AddFriendCommand(AuthenticationManager authManager,
                            FriendshipService friendshipService,
                            NotificationService notificationService) {
        this.authManager = authManager;
        this.friendshipService = friendshipService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, AuthenticationException {
        validate(inputTokens);

        String addFriendUsername = inputTokens[1];
        try {
            friendshipService.addFriendship(authManager.getAuthenticatedUser().getUsername(),
                    addFriendUsername);

            notificationService.addNotification(
                    authManager.getAuthenticatedUser().getFullName() +
                            " added you as a friend!",
                    addFriendUsername);

            out.println("Successfully added " + addFriendUsername + " to your friend list!");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate(String[] inputTokens)
            throws InvalidCommandInputException, NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        if (inputTokens.length < 2) {
            throw new InvalidCommandInputException(
                    "Invalid command! Command must be add-friend <username>!");
        }
    }

}