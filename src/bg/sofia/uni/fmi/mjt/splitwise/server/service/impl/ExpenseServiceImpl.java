package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.ExpenseCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ExpenseServiceImpl implements ExpenseService {

    private final UserService userService;
    private final ObligationService obligationService;
    private final ExpenseCsvProcessor expenseCsvProcessor;
    private final List<Expense> expenses;

    public ExpenseServiceImpl(UserService userService,
                              ObligationService obligationService) {
        this.userService = userService;
        this.obligationService = obligationService;
        this.expenseCsvProcessor = new ExpenseCsvProcessor(userService);
        this.expenses = expenseCsvProcessor.loadExpensesFromCsvFile();
    }

    @Override
    public Collection<Expense> getExpensesPaidByUser(String username)
            throws UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username" + username
                    + " was not found!");
        }

        return expenses
                .stream()
                .filter(expense -> expense.payer().getUsername().equals(username))
                .toList();
    }

    @Override
    public void addExpense(String payerUsername, String description,
                           double amount, Set<String> participantsUsernames)
            throws UserNotFoundException {
        if (payerUsername == null || description == null || participantsUsernames == null
                || payerUsername.isBlank() || description.isBlank()
                || participantsUsernames.isEmpty()) {
            throw new IllegalArgumentException("Invalid data!"
                    + " Can't add expense to database!");
        }
        Optional<User> payer = userService.findUserByUsername(payerUsername);
        if (payer.isEmpty()) {
            throw new UserNotFoundException("User with username " + payerUsername
                    + " was not found!");
        }

        Set<User> participants = new HashSet<>();
        for (String username : participantsUsernames) {
            Optional<User> user = userService.findUserByUsername(username);
            if (user.isEmpty()) {
                throw new UserNotFoundException("User with username "
                        + username + " was not found!");
            }
            participants.add(user.get());
        }

        Expense expense = new Expense(payer.get(), description, amount, participants);
        expenseCsvProcessor.writeExpenseToCsvFile(expense);
        double amountPerUser = amount / (participants.size() + 1);
        participants.forEach(participant ->
                obligationService.updateObligation(payer.get(), participant, amountPerUser));
    }

}
