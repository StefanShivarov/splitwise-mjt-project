package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.PrintWriter;
import java.util.Set;

public class SplitWithFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final FriendshipService friendshipService;
    private final ExpenseService expenseService;
    private final NotificationService notificationService;
    private static final int MIN_TOKENS_AMOUNT = 4;
    private static final int AMOUNT_INDEX = 1;
    private static final int USERNAME_INDEX = 2;
    private static final int DESC_INDEX = 3;

    public SplitWithFriendCommand(AuthenticationManager authManager,
                                  FriendshipService friendshipService,
                                  ExpenseService expenseService,
                                  NotificationService notificationService) {
        this.authManager = authManager;
        this.friendshipService = friendshipService;
        this.expenseService = expenseService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate(inputTokens);

        double amount = Double.parseDouble(inputTokens[AMOUNT_INDEX]);
        String friendUsername = inputTokens[USERNAME_INDEX];
        String description = inputTokens[DESC_INDEX];

        try {
            if (!friendshipService.checkFriendship(
                    authManager.getAuthenticatedUser().getUsername(), friendUsername)) {
                out.println("Error! " + friendUsername + " is not your friend!");
            }

            expenseService.addExpense(authManager.getAuthenticatedUser().getUsername(),
                    description,
                    amount,
                    Set.of(friendUsername));

            notificationService.addNotification(
                    String.format("%s paid %s (%s each) for you [%s].",
                            authManager.getAuthenticatedUser().getFullName(),
                            FormatterProvider.getDecimalFormat().format(amount),
                            FormatterProvider.getDecimalFormat().format(amount / 2),
                            description),
                    friendUsername);

            out.println("You split " + FormatterProvider.getDecimalFormat().format(amount)
                    + " with " + friendUsername + ".");
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
                    + "Split command must be split <amount> <username> <desc>.");
        }
    }

}
