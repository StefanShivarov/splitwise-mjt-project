package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupService groupService;
    private final NotificationService notificationService;

    public CreateGroupCommand(AuthenticationManager authManager,
                              GroupService groupService,
                              NotificationService notificationService) {
        this.authManager = authManager;
        this.groupService = groupService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, AuthenticationException {
        validate(inputTokens);

        String groupName = inputTokens[1];
        Set<String> usernames = Arrays.stream(inputTokens)
                .skip(2)
                .collect(Collectors.toSet());
        usernames.add(authManager.getAuthenticatedUser().getUsername());

        try {
            for (String username : usernames) {
                if (!username.equals(authManager.getAuthenticatedUser().getUsername())) {
                    notificationService.addNotification(
                            authManager.getAuthenticatedUser().getFullName() +
                                    "added you to group" + groupName + ".",
                            username);
                }
            }
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }

        groupService.addGroup(groupName, usernames);
        out.println("Successfully created group " + groupName + "!");
    }

    private void validate(String[] inputTokens)
            throws InvalidCommandInputException, NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException(
                    "Invalid command! Group must have a name and at least two more members!");
        }
    }

}
