package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateGroupCommandTest {

    @Mock
    private AuthenticationManager authManagerMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private NotificationService notificationServiceMock;

    @InjectMocks
    private CreateGroupCommand createGroupCommand;

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
        assertThrows(NotAuthenticatedException.class,
                () -> createGroupCommand.execute(
                        new String[]{"create-group", "newgroup", "user1", "user2", "user3"},
                        out),
                "should throw NotAuthenticatedException if client is not authenticated!");

        when(authManagerMock.isAuthenticated())
                .thenReturn(true);
        assertThrows(InvalidCommandInputException.class,
                () -> createGroupCommand.execute(new String[]{"create-group"}, out),
                "should throw InvalidCommandInputException if command input is invalid!");
    }

    @Test
    void testExecute() throws AuthenticationException,
            InvalidCommandInputException, UserNotFoundException {
        when(authManagerMock.isAuthenticated())
                .thenReturn(true);
        when(authManagerMock.getAuthenticatedUser())
                .thenReturn(new User("ivan", "12345"));

        String exceptionMessage = "User with username user1 was not found!";
        doThrow(new UserNotFoundException(exceptionMessage))
                .when(notificationServiceMock)
                .addNotification(anyString(), anyString());

        createGroupCommand.execute(
                new String[]{"create-group", "newgroup", "user1", "user2", "user3"}, out);

        assertEquals(exceptionMessage + System.lineSeparator(),
                stringWriter.toString(),
                "should return correct output!");

        out.flush();
        stringWriter.getBuffer().setLength(0);

        doNothing()
                .when(notificationServiceMock)
                .addNotification(anyString(), anyString());

        createGroupCommand.execute(
                new String[]{"create-group", "newgroup", "user1", "user2", "user3"}, out);

        verify(groupServiceMock, times(1))
                .addGroup(anyString(), any());

        assertEquals("Successfully created group newgroup!" + System.lineSeparator(),
                stringWriter.toString(),
                "should return correct output!");
    }

}
