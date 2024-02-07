package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.ExpenseCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

    @Mock
    private ExpenseCsvProcessor expenseCsvProcessorMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ObligationService obligationServiceMock;

    private ExpenseServiceImpl expenseService;

    @BeforeEach
    void setUp() {
        List<Expense> mockData = new ArrayList<>();
        mockData.add(new Expense(
                new User("ivan", "12345"),
                "rent",
                349.99,
                Set.of(new User("landlord", "pass"))
        ));

        mockData.add(new Expense(
                new User("georgi", "0000"),
                "food",
                150.45,
                Set.of(new User("ivan", "12345"),
                        new User("stamat", "2222"))
        ));
        when(expenseCsvProcessorMock.loadExpensesFromCsvFile())
                .thenReturn(mockData);

        expenseService = new ExpenseServiceImpl(expenseCsvProcessorMock,
                userServiceMock, obligationServiceMock);
    }

    @Test
    void testGetExpensesPaidByUserThrows() {
        when(userServiceMock.findUserByUsername("kalin"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> expenseService.getExpensesPaidByUser("kalin"),
                "getExpensesPaidByUser should throw UserNotFoundException " +
                        "if user does not exist!");
    }

    @Test
    void testGetExpensesPaidByUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        List<Expense> userExpenses = expenseService
                .getExpensesPaidByUser("ivan").stream().toList();

        assertEquals(1, userExpenses.size(),
                "should return correct collection");

        assertEquals("rent", userExpenses.get(0).description(),
                "should return correct collection");

        assertEquals(349.99, userExpenses.get(0).amount(),
                "should return correct collection");

        assertEquals("349.99 for landlord; Reason: [rent]",
                userExpenses.get(0).toString(),
                "should return correct collection");

        assertEquals(1, userExpenses.get(0).participants().size(),
                "should return correct collection");

        assertEquals(new Expense(
                new User("ivan", "12345"),
                "rent",
                349.99,
                Set.of(new User("landlord", "pass"))), userExpenses.get(0),
                "should return correct collection");
    }

    @Test
    void testAddExpenseThrows() {
        when(userServiceMock.findUserByUsername("stamat"))
                .thenReturn(Optional.empty());
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        assertThrows(IllegalArgumentException.class,
                () -> expenseService.addExpense(null, "desc", 12, null),
                "should throw IllegalArgumentException for invalid data!");

        assertThrows(UserNotFoundException.class,
                () -> expenseService.addExpense("ivan", "desc", 10, Set.of("stamat")),
                "should throw UserNotFoundException if participant does not exist!");

        assertThrows(UserNotFoundException.class,
                () -> expenseService.addExpense("stamat", "desc", 10, Set.of("ivan")),
                "should throw UserNotFoundException if payer does not exist!");
    }

    @Test
    void testAddExpense() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("stamat"))
                .thenReturn(Optional.of(new User("stamat", "2222")));
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        expenseService.addExpense("stamat", "desc", 10, Set.of("ivan"));
        assertEquals(1, expenseService.getExpensesPaidByUser("stamat").size());

        verify(obligationServiceMock, times(1))
                .updateObligation(any(User.class), any(User.class), anyDouble());
    }

}
