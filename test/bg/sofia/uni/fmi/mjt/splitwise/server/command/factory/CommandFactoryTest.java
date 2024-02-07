package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ApprovePaymentCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.LogoutCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.RegisterCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowExpensesCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowFriendsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowGroupsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.SplitWithGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.SplitWithFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CommandFactoryTest {

    private static CommandFactory commandFactory;

    @BeforeAll
    static void setUp() {
        UserService userServiceMock = mock();
        AuthenticationManager authenticationManagerMock = mock();
        ObligationService obligationServiceMock = mock();
        FriendshipService friendshipServiceMock = mock();
        GroupService groupServiceMock = mock();
        ExpenseService expenseServiceMock = mock();
        NotificationService notificationServiceMock = mock();
        commandFactory = new CommandFactory(
                authenticationManagerMock,
                userServiceMock,
                friendshipServiceMock,
                groupServiceMock,
                expenseServiceMock,
                obligationServiceMock,
                notificationServiceMock);
    }

    @Test
    void testCreateCommandReturnsCorrectCommand() throws InvalidCommandInputException {
        assertInstanceOf(HelpCommand.class,
                commandFactory.createCommand("help"),
                "help should return HelpCommand!");
        assertInstanceOf(LoginCommand.class,
                commandFactory.createCommand("login"),
                "login should return LoginCommand!");
        assertInstanceOf(LogoutCommand.class,
                commandFactory.createCommand("logout"),
                "logout should return LogoutCommand!");
        assertInstanceOf(RegisterCommand.class,
                commandFactory.createCommand("register"),
                "register should return RegisterCommand!");
        assertInstanceOf(AddFriendCommand.class,
                commandFactory.createCommand("add-friend"),
                "add-friend should return AddFriendCommand!");
        assertInstanceOf(ShowFriendsCommand.class,
                commandFactory.createCommand("my-friends"),
                "my-friends should return ShowFriendsCommand!");
        assertInstanceOf(CreateGroupCommand.class,
                commandFactory.createCommand("create-group"),
                "create-group should return CreateGroupCommand!");
        assertInstanceOf(ShowGroupsCommand.class,
                commandFactory.createCommand("my-groups"),
                "my-groups should return ShowGroupsCommand!");
        assertInstanceOf(SplitWithFriendCommand.class,
                commandFactory.createCommand("split"),
                "split should return SplitWithFriendCommand!");
        assertInstanceOf(SplitWithGroupCommand.class,
                commandFactory.createCommand("split-group"),
                "split-group should return SplitWithGroupCommand!");
        assertInstanceOf(ApprovePaymentCommand.class,
                commandFactory.createCommand("approve-payment"),
                "approve-payment should return ApprovePaymentCommand!");
        assertInstanceOf(ShowExpensesCommand.class,
                commandFactory.createCommand("my-expenses"),
                "my-expenses should return ShowExpensesCommand!");
    }

    @Test
    void testCreateCommandThrowsInvalidCommandInputException() {
        assertThrows(InvalidCommandInputException.class,
                () -> commandFactory.createCommand("invalid"),
                "createCommand should throw " +
                        "InvalidCommandInputException for invalid command!");
    }

}
