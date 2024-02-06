package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ApprovePaymentCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.LogoutCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.RegisterCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowExpensesCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowFriendsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.ShowGroupsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.SplitWIthGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.impl.SplitWithFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

public class CommandFactory {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final ObligationService obligationService;
    private final NotificationService notificationService;

    public CommandFactory(AuthenticationManager authManager,
                          UserService userService,
                          FriendshipService friendshipService,
                          GroupService groupService,
                          ExpenseService expenseService,
                          ObligationService obligationService,
                          NotificationService notificationService) {
        this.authManager = authManager;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
    }

    public Command createCommand(String command) throws InvalidCommandInputException {
        return switch (command) {
            case "help" -> new HelpCommand();
            case "login" -> new LoginCommand(authManager, notificationService);
            case "logout" -> new LogoutCommand(authManager);
            case "register" -> new RegisterCommand(authManager, userService);
            case "add-friend" -> new AddFriendCommand(authManager, friendshipService, notificationService);
            case "my-friends" -> new ShowFriendsCommand(authManager, friendshipService, obligationService);
            case "create-group" -> new CreateGroupCommand(authManager, groupService, notificationService);
            case "my-groups" -> new ShowGroupsCommand(authManager, groupService, obligationService);
            case "split" -> new SplitWithFriendCommand(authManager, friendshipService,
                    expenseService, notificationService);
            case "split-group" -> new SplitWIthGroupCommand(authManager, groupService,
                    expenseService, notificationService);
            case "approve-payment" -> new ApprovePaymentCommand(authManager, obligationService,
                    notificationService);
            case "my-expenses" -> new ShowExpensesCommand(authManager, expenseService);
            default -> throw new InvalidCommandInputException(
                    "Invalid command! Cannot recognize command " + command + "!");
        };
    }

}
