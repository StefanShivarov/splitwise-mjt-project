package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowGroupsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShowGroupsCommandTest {

    @Mock
    private AuthenticationManager authManagerMock;

    @Mock
    private GroupService groupServiceMock;

    @Mock
    private ObligationService obligationServiceMock;

    @InjectMocks
    private ShowGroupsCommand showGroupsCommand;
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
                () -> showGroupsCommand.execute(new String[]{"my-groups"}, out),
                "should throw NotAuthenticatedException if client is not authenticated!");
    }

    @Test
    void testExecute() throws UserNotFoundException,
            NotAuthenticatedException, InvalidCommandInputException {
        when(authManagerMock.isAuthenticated())
                .thenReturn(true);

        when(authManagerMock.getAuthenticatedUser())
                .thenReturn(new User("ivan", "12345"));

        when(groupServiceMock.getGroupsForUser("ivan"))
                .thenReturn(Set.of());

        showGroupsCommand.execute(new String[]{"my-groups"}, out);
        assertEquals("No groups to show." + System.lineSeparator(),
                stringWriter.toString(),
                "should print correct output!");

        out.flush();
        stringWriter.getBuffer().setLength(0);

        when(groupServiceMock.getGroupsForUser("ivan"))
                .thenReturn(Set.of(
                        new Group("testgroup", Set.of(
                                new User("ivan", "12345"),
                                new User("dragan", "1111"),
                                new User("petkan", "5555")
                        ))));

        when(obligationServiceMock
                .getObligationStatusWithUserForLoggedInUser("ivan", "dragan"))
                .thenReturn("ivan");
        when(obligationServiceMock
                .getObligationStatusWithUserForLoggedInUser("ivan", "petkan"))
                .thenReturn("petkan : Owes you 12.50");

        showGroupsCommand.execute(new String[]{"my-groups"}, out);
        assertEquals("Groups:" + System.lineSeparator() + "* testgroup" + System.lineSeparator()
                        + "-- ivan" + System.lineSeparator() + "-- petkan : Owes you 12.50"
                        + System.lineSeparator(),
                stringWriter.toString(),
                "should print correct output!");
    }

}
