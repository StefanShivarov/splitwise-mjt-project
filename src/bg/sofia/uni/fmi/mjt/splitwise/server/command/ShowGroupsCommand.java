package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShowGroupsCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupService groupService;
    private final ObligationService obligationService;

    public ShowGroupsCommand(AuthenticationManager authManager,
                             GroupService groupService,
                             ObligationService obligationService) {
        this.authManager = authManager;
        this.groupService = groupService;
        this.obligationService = obligationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate();

        StringBuilder groupsOutput = new StringBuilder("Groups:")
                .append(System.lineSeparator());

        try {
            Collection<Group> groups = groupService
                    .getGroupsForUser(authManager.getAuthenticatedUser().getUsername());

            if (groups.isEmpty()) {
                out.println("No groups to show.");
                return;
            }

            groupsOutput.append(groups
                    .stream()
                    .map(group -> "* " +
                            group.getName() +
                            System.lineSeparator() +
                            group.getMembers()
                                    .stream()
                                    .map(User::getUsername)
                                    .filter(username -> !username.equals(
                                            authManager.getAuthenticatedUser().getUsername()))
                                    .map(username -> {
                                        try {
                                            return obligationService
                                                    .getObligationStatusWithUserForLoggedInUser(
                                                            authManager
                                                                    .getAuthenticatedUser()
                                                                    .getUsername(),
                                                            username);
                                        } catch (UserNotFoundException e) {
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .sorted()
                                    .map(str -> "-- " + str)
                                    .collect(Collectors.joining(System.lineSeparator()))
                    )
                    .collect(Collectors.joining(System.lineSeparator())));

            out.println(groupsOutput);

        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate() throws NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

}
