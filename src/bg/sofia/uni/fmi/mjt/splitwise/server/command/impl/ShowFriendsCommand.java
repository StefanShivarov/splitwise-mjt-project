package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShowFriendsCommand implements Command {

    private final AuthenticationManager authManager;
    private final FriendshipService friendshipService;
    private final ObligationService obligationService;

    public ShowFriendsCommand(AuthenticationManager authManager,
                              FriendshipService friendshipService,
                              ObligationService obligationService) {
        this.authManager = authManager;
        this.friendshipService = friendshipService;
        this.obligationService = obligationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate();

        StringBuilder friendListOutput = new StringBuilder("Friends: ")
                .append(System.lineSeparator());
        try {
            Collection<User> friends = friendshipService
                    .getFriendsForUser(authManager.getAuthenticatedUser().getUsername());

            if (friends.isEmpty()) {
                out.println("No friends to show.");
                return;
            }

            friendListOutput.append(getFriendsOutput(friends, out));
            out.println(friendListOutput);
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate() throws NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

    private String getFriendsOutput(Collection<User> friends, PrintWriter out) {
        return friends
                .stream()
                .map(User::getUsername)
                .map(username -> {
                    try {
                        return obligationService
                                .getObligationStatusWithUserForLoggedInUser(
                                        authManager.getAuthenticatedUser().getUsername(),
                                        username);
                    } catch (UserNotFoundException e) {
                        out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(System.lineSeparator()));
    }

}
