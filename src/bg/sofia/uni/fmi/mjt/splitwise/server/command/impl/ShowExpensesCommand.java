package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;

import java.io.PrintWriter;
import java.util.stream.Collectors;

public class ShowExpensesCommand implements Command {

    private final AuthenticationManager authManager;
    private final ExpenseService expenseService;

    public ShowExpensesCommand(AuthenticationManager authManager, ExpenseService expenseService) {
        this.authManager = authManager;
        this.expenseService = expenseService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException, NotAuthenticatedException {
        validate();

        try {
            out.println("--- My expenses ---" + System.lineSeparator() +
                    expenseService
                            .getExpensesPaidByUser(
                                    authManager.getAuthenticatedUser().getUsername())
                            .stream()
                            .map(Expense::toString)
                            .collect(Collectors.joining(System.lineSeparator())));
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
