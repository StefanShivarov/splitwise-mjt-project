package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginCommandTest {

    @Mock
    private AuthenticationManager authManagerMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private LoginCommand loginCommand;
    private StringWriter stringWriter;
    private PrintWriter out;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        out = new PrintWriter(stringWriter);
    }

    @Test
    void testValidation() {
        when(authManagerMock.isAuthenticated())
                .thenReturn(false);
        assertThrows(InvalidCommandInputException.class,
                () -> loginCommand.execute(new String[]{"login", "ivan"}, out),
                "should throw InvalidCommandInputException for invalid command input!");

        when(authManagerMock.isAuthenticated())
                .thenReturn(true);
        assertThrows(AlreadyAuthenticatedException.class,
                () -> loginCommand.execute(new String[]{"login", "ivan", "0000"}, out),
                "should throw AlreadyAuthenticatedException " +
                        "if client is already authenticated!");
    }

    @Test
    void testExecute() throws AlreadyAuthenticatedException, InvalidCommandInputException, UserNotFoundException {
        when(authManagerMock.isAuthenticated())
                .thenReturn(false);
        when(authManagerMock.authenticate(anyString(), anyString()))
                .thenReturn(false);

        loginCommand.execute(new String[]{"login", "ivan", "0000"}, out);
        assertEquals("Invalid credentials! Login unsuccessful!" + System.lineSeparator(),
                stringWriter.toString(),
                "should return correct response!");

        out.flush();
        stringWriter.getBuffer().setLength(0);

        when(authManagerMock.authenticate(anyString(), anyString()))
                .thenReturn(true);
        when(authManagerMock.getAuthenticatedUser())
                .thenReturn(new User("ivan", "0000"));

        loginCommand.execute(new String[]{"login", "ivan", "0000"}, out);

        verify(notificationServiceMock, times(1))
                .getUnseenNotificationsForUser("ivan");

        verify(notificationServiceMock, times(1))
                .getNotificationsOutput(any());

        verify(notificationServiceMock, times(1))
                .markNotificationsAsSeen(any());

        assertTrue(stringWriter.toString().startsWith("Welcome, ivan!"),
                "should return correct response!");
    }

}
