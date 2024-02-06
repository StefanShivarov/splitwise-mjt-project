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

public class SplitWithGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final NotificationService notificationService;
    private static final int MIN_TOKENS_AMOUNT = 4;
    private static final int AMOUNT_INDEX = 1;
    private static final int GROUP_NAME_INDEX = 2;
    private static final int DESC_INDEX = 3;

    public SplitWithGroupCommand(AuthenticationManager authManager,
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

        double amount = Double.parseDouble(inputTokens[AMOUNT_INDEX]);
        String groupName = inputTokens[GROUP_NAME_INDEX];
        String description = inputTokens[DESC_INDEX];

        Optional<Group> group = groupService.findGroupByName(groupName);
        if (group.isEmpty()) {
            out.println("Error! You are not part of a group called " + groupName + "!");
            return;
        }

        Set<String> usernames = group.get().getMembers()
                .stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(
                        authManager.getAuthenticatedUser().getUsername()))
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

            out.println("You split " + FormatterProvider.getDecimalFormat().format(amount)
                    + " with group" + groupName + ".");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate(String[] inputTokens)
            throws NotAuthenticatedException, InvalidCommandInputException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        if (inputTokens.length < MIN_TOKENS_AMOUNT) {
            throw new InvalidCommandInputException("Invalid command! "
                    + "Split command must be split-group <amount> <group_name> <desc>.");
        }
    }

}
