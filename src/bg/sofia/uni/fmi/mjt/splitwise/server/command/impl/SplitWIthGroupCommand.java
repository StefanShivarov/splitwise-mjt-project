package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SplitWIthGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final NotificationService notificationService;

    public SplitWIthGroupCommand(AuthenticationManager authManager,
                                 GroupService groupService,
                                 ExpenseService expenseService,
                                 NotificationService notificationService) {
        this.authManager = authManager;
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate(inputTokens);

        double amount = Double.parseDouble(inputTokens[1]);
        String groupName = inputTokens[2];
        String description = inputTokens[3];

        Optional<Group> group = groupService.findGroupByName(groupName);
        if (group.isEmpty()) {
            out.println("Error! You are not part of a group called " + groupName + "!");
            return;
        }

        Set<String> usernames = group.get().getMembers()
                .stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(authManager.getAuthenticatedUser().getUsername()))
                .collect(Collectors.toSet());

        try {
            expenseService.addExpense(authManager.getAuthenticatedUser().getUsername(),
                    description,
                    amount,
                    usernames);

            notificationService.addNotification(
                    String.format("%s paid %s (%s each) for group %s [%s].",
                            authManager.getAuthenticatedUser().getFullName(),
                            FormatterProvider.getDecimalFormat().format(amount),
                            FormatterProvider.getDecimalFormat()
                                    .format(amount / (usernames.size() + 1)),
                            groupName,
                            description),
                    usernames);

            out.println("You split " + FormatterProvider.getDecimalFormat().format(amount) +
                    " with group" + groupName + ".");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate(String[] inputTokens)
            throws NotAuthenticatedException, InvalidCommandInputException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException("Invalid command! " +
                    "Split command must be split-group <amount> <group_name> <desc>.");
        }
    }

}
