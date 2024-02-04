package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;

import java.util.Collection;
import java.util.Set;

public interface ExpenseService {

    Collection<Expense> getExpensesPaidByUser(String username) throws UserNotFoundException;

    void addExpense(String payerUsername, String description, double amount, Set<String> participantsUsernames)
            throws UserNotFoundException;

}
