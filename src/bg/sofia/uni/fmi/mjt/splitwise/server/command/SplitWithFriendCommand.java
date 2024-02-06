package bg.sofia.uni.fmi.mjt.splitwise.server.command;

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
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException("Invalid command! " +
                    "Split command must be split <amount> <username> <desc>.");
        }

        double amount = Double.parseDouble(inputTokens[1]);
        String friendUsername = inputTokens[2];
        String description = inputTokens[3];

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

}
