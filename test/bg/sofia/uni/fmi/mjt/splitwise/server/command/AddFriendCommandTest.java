package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddFriendCommandTest {

    @Mock
    private AuthenticationManager authManagerMock;

    @Mock
    private FriendshipService friendshipServiceMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private AddFriendCommand addFriendCommand;
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
                .thenReturn(true);

        assertThrows(InvalidCommandInputException.class,
                () -> addFriendCommand.execute(new String[]{"add-friend"}, out),
                "should throw invalid command input exception for invalid command input");

        when(authManagerMock.isAuthenticated())
                .thenReturn(false);

        assertThrows(NotAuthenticatedException.class,
                () -> addFriendCommand.execute(new String[]{"add-friend", "username"}, out),
                "should throw NotAuthenticatedException if client is not authenticated");
    }

    @Test
    void testExecute() throws AuthenticationException, InvalidCommandInputException {
        when(authManagerMock.isAuthenticated())
                .thenReturn(true);

        when(authManagerMock.getAuthenticatedUser())
                .thenReturn(new User("ivan", "12345", "Ivan", "Ivanov"));

        addFriendCommand.execute(new String[]{"add-friend", "username"}, out);
        assertEquals("Successfully added username to your friend list!" + System.lineSeparator(),
                stringWriter.toString(),
                "should print out the correct message");
    }

}
