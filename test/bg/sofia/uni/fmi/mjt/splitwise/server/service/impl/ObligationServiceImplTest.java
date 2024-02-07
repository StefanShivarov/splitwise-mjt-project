package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.ObligationCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObligationServiceImplTest {

    @Mock
    private ObligationCsvProcessor obligationCsvProcessorMock;

    @Mock
    private UserService userServiceMock;

    private ObligationServiceImpl obligationService;

    @BeforeEach
    void setUp() {
        Set<Obligation> mockData = new HashSet<>();
        mockData.add(new Obligation(
                new User("ivan", "12345"),
                new User("valio", "2222"),
                -10.50
        ));

        mockData.add(new Obligation(
                new User("ivan", "12345"),
                new User("stamat", "pass"),
                21.99
        ));

        when(obligationCsvProcessorMock.loadObligationsFromCsvFile())
                .thenReturn(mockData);

        obligationService = new ObligationServiceImpl(
                obligationCsvProcessorMock, userServiceMock);
    }

    @Test
    void testGetObligationsForUserThrows() {
        when(userServiceMock.findUserByUsername("mitko"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> obligationService.getObligationsForUser(null),
                "should throw exception if username is blank or null!");

        assertThrows(IllegalArgumentException.class,
                () -> obligationService.getObligationsForUser("  "),
                "should throw exception if username is blank or null!");

        assertThrows(UserNotFoundException.class,
                () -> obligationService.getObligationsForUser("mitko"),
                "should throw exception if user doesn't exist!");
    }

    @Test
    void testGetObligationsForUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        List<Obligation> obligations = obligationService
                .getObligationsForUser("ivan")
                .stream()
                .toList();

        assertEquals(2, obligations.size(),
                "should return correct collection");

        assertEquals(-10.50, obligations.get(0).getBalance());
        assertEquals(21.99, obligations.get(1).getBalance());
    }

    @Test
    void testFindObligationByUsersThrows() {
        when(userServiceMock.findUserByUsername("mitko"))
                .thenReturn(Optional.empty());
        when(userServiceMock.findUserByUsername("pavel"))
                .thenReturn(Optional.of(new User("pavel", "5555")));

        assertThrows(UserNotFoundException.class,
                () -> obligationService.findObligationByUsers("mitko", "pavel"),
                "should throw UserNotFoundException if user doesnt exist!");

        assertThrows(UserNotFoundException.class,
                () -> obligationService.findObligationByUsers("pavel", "mitko"),
                "should throw UserNotFoundException if user doesnt exist!");
    }

    @Test
    void testFindObligationByUsers() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        when(userServiceMock.findUserByUsername("valio"))
                .thenReturn(Optional.of(new User("valio", "2222")));

        assertTrue(obligationService.findObligationByUsers("ivan", "valio").isPresent(),
                "obligation should be present if it exists");
    }

    @Test
    void updateObligationThrows() {
        when(userServiceMock.findUserByUsername("mitko"))
                .thenReturn(Optional.empty());
        when(userServiceMock.findUserByUsername("pavel"))
                .thenReturn(Optional.of(new User("pavel", "5555")));

        assertThrows(UserNotFoundException.class,
                () -> obligationService.updateObligation("mitko", "pavel", 20),
                "should throw UserNotFoundException if user doesnt exist!");

        assertThrows(UserNotFoundException.class,
                () -> obligationService.updateObligation("pavel", "mitko", 20),
                "should throw UserNotFoundException if user doesnt exist!");

        assertThrows(IllegalArgumentException.class,
                () -> obligationService.updateObligation(null, new User("valio", "2222"), 10),
                "should throw IllegalArgumentException if a null user is passed!");
    }

    @Test
    void testUpdateObligation() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        when(userServiceMock.findUserByUsername("valio"))
                .thenReturn(Optional.of(new User("valio", "2222")));

        assertDoesNotThrow(() -> obligationService.updateObligation("ivan", "valio", 5),
                "should not throw for correct data!");
        Optional<Obligation> obligation = obligationService.findObligationByUsers("ivan", "valio");
        assertTrue(obligation.isPresent());
        assertEquals(-15.50,
                obligationService.findObligationByUsers("ivan", "valio")
                .get()
                .getBalance(),
                "should update obligation correctly!");
    }

    @Test
    void testGetObligationStatusWithUserForLoggedInUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        when(userServiceMock.findUserByUsername("stamat"))
                .thenReturn(Optional.of(new User("stamat", "pass")));
        when(userServiceMock.findUserByUsername("valio"))
                .thenReturn(Optional.of(new User("valio", "2222")));

        assertEquals("stamat : You owe 21.99", obligationService
                .getObligationStatusWithUserForLoggedInUser("ivan", "stamat"),
                "should return correct status string");

        assertEquals("ivan : Owes you 21.99", obligationService
                .getObligationStatusWithUserForLoggedInUser("stamat", "ivan"),
                "should return correct status string");

        assertEquals("ivan : You owe 10.50", obligationService
                .getObligationStatusWithUserForLoggedInUser("valio", "ivan"),
                "should return correct status string");

        assertEquals("valio : Owes you 10.50", obligationService
                .getObligationStatusWithUserForLoggedInUser("ivan", "valio"),
                "should return correct status string");
    }

    @Test
    void testAddObligationThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> obligationService.addObligation(null, null, 20),
                "should throw IllegalArgumentException if arguments are null");
    }

    @Test
    void testAddObligation() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("joro"))
                .thenReturn(Optional.of(new User("joro", "123")));
        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.of(new User("georgi", "1234")));
        obligationService.addObligation(
                new User("joro", "123"),
                new User("georgi", "1234"),
                60.50);

        assertTrue(obligationService.findObligationByUsers("joro", "georgi").isPresent());
    }

}
