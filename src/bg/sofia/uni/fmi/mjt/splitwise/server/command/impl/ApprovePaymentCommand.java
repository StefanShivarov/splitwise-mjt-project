package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.PrintWriter;

public class ApprovePaymentCommand implements Command {

    private final AuthenticationManager authManager;
    private final ObligationService obligationService;
    private final NotificationService notificationService;
    private static final int MIN_TOKENS_AMOUNT = 3;
    private static final int AMOUNT_INDEX = 1;
    private static final int USERNAME_INDEX = 2;

    public ApprovePaymentCommand(AuthenticationManager authManager,
                                 ObligationService obligationService,
                                 NotificationService notificationService) {
        this.authManager = authManager;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate(inputTokens);

        double amount = Double.parseDouble(inputTokens[AMOUNT_INDEX]);
        String payerUsername = inputTokens[USERNAME_INDEX];

        try {
            obligationService.updateObligation(payerUsername,
                    authManager.getAuthenticatedUser().getUsername(),
                    amount);

            notificationService.addNotification(
                    String.format("%s approved your payment of %s.",
                            authManager.getAuthenticatedUser().getFullName(),
                            FormatterProvider.getDecimalFormat().format(amount)),
                    payerUsername);

            out.println(payerUsername + " payed you "
                    + FormatterProvider.getDecimalFormat().format(amount) + ".");
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
                    + "Command must be approve-payment <amount> <username>!");
        }
    }

}
