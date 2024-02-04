package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.ExpenseCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ExpenseServiceImpl implements ExpenseService {

    private final UserService userService;
    private final ExpenseCsvProcessor expenseCsvProcessor;
    private final List<Expense> expenses;

    public ExpenseServiceImpl(UserService userService) {
        this.userService = userService;
        this.expenseCsvProcessor = new ExpenseCsvProcessor(userService);
        this.expenses = expenseCsvProcessor.loadExpensesFromCsvFile();
    }

    @Override
    public Collection<Expense> getExpensesPaidByUser(String username) throws UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username" + username + " was not found!");
        }
        //TODO: implement

        return null;
    }

}
