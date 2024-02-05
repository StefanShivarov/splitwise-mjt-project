package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final ObligationService obligationService;
    private final NotificationService notificationService;
    private static final DecimalFormat decimalFormat = new DecimalFormat(
            "#.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static final String UNAUTHENTICATED_MESSAGE = "Please register or login to get started.";
    private static final String CLIENT_WELCOME_MESSAGE = "---------Welcome to Splitwise!---------";

    public ClientHandler(Socket socket,
                         AuthenticationManager authManager,
                         UserService userService,
                         FriendshipService friendshipService,
                         GroupService groupService,
                         ExpenseService expenseService,
                         ObligationService obligationService,
                         NotificationService notificationService) {
        this.socket = socket;
        this.authManager = authManager;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Handler " + socket.getRemoteSocketAddress());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(CLIENT_WELCOME_MESSAGE);
            out.println(UNAUTHENTICATED_MESSAGE);
            String input;
            while ((input = in.readLine()) != null) {
                System.out.println(input);
                handleInput(input, out);

                if (!authManager.isAuthenticated()) {
                    out.println(UNAUTHENTICATED_MESSAGE);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleInput(String input, PrintWriter out) {
        String[] inputTokens = input.split("\\s+");
        try {
            switch (inputTokens[0]) {
                case "help" -> showHelpInfo(out);
                case "login" -> handleLogin(inputTokens, out);
                case "logout" -> handleLogout(out);
                case "register" -> handleRegister(inputTokens, out);
                case "add-friend" -> handleAddFriend(inputTokens, out);
                case "create-group" -> handleCreateGroup(inputTokens, out);
                case "my-friends" -> handleShowFriends(out);
                case "my-groups" -> handleShowGroups(out);
                case "notifications" -> out.println(getUnseenNotifications());
                case "old-notifications" -> showOldNotifications(out);
                case "split" -> handleSplitWithFriend(inputTokens, out);
                case "split-group" -> handleSplitWithGroup(inputTokens, out);
                case "payed" -> handleReceivePayment(inputTokens, out);
                default -> out.println("Invalid command! Try again!");
            }
        } catch (InvalidCommandInputException e) {
            out.println(e.getMessage());
        }
    }

    private void showHelpInfo(PrintWriter out) {
        //TODO: make help text
    }

    private void handleLogin(String[] inputTokens, PrintWriter out) throws InvalidCommandInputException {
        if (authManager.isAuthenticated()) {
            out.println("You don't have access to this command! You are already logged in.");
            return;
        }

        if (inputTokens.length < 3) {
            throw new InvalidCommandInputException("Invalid command! Login must be login <username> <password>!");
        }

        if (authManager.authenticate(inputTokens[1], inputTokens[2])) {
            out.println("Welcome, " + authManager.getAuthenticatedUser().getUsername() + "!"
                    + System.lineSeparator() + getUnseenNotifications());
        } else {
            out.println("Invalid credentials! Login unsuccessful!");
        }
    }

    private String getUnseenNotifications() {
        try {
            Collection<Notification> notifications = notificationService
                    .getUnseenNotificationsForUser(authManager.getAuthenticatedUser().getUsername());

            if (notifications.isEmpty()) {
                return "No new notifications to show.";
            }

            notificationService.markNotificationsAsSeen(notifications);

            return "*** Notifications ***" +
                    System.lineSeparator() +
                    notifications
                            .stream()
                            .map(Notification::toString)
                            .collect(Collectors.joining(System.lineSeparator()));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void showOldNotifications(PrintWriter out) {
        try {
            Collection<Notification> notifications = notificationService
                    .getAllNotificationsForUser(authManager.getAuthenticatedUser().getUsername());

            if (notifications.isEmpty()) {
                out.println("No notifications to show.");
                return;
            }

            out.println("--- Old notifications ---" +
                    System.lineSeparator() + notifications
                    .stream()
                    .map(Notification::toString)
                    .collect(Collectors.joining(System.lineSeparator())));
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleLogout(PrintWriter out) {
        if (!authManager.isAuthenticated()) {
            out.println("You don't have access to this command!" +
                    " You are not currently logged in." +
                    " Please log in first before attempting to logout.");
            return;
        }

        authManager.logout();
    }

    private void handleRegister(String[] inputTokens, PrintWriter out) {
        if (authManager.isAuthenticated()) {
            out.println("You don't have access to this command! You are already logged in.");
            return;
        }

        switch (inputTokens.length) {
            case 3 -> userService.addUser(inputTokens[1], inputTokens[2], "", "");
            case 4 -> userService.addUser(inputTokens[1], inputTokens[2], inputTokens[3], "");
            case 5 -> userService.addUser(inputTokens[1], inputTokens[2], inputTokens[3], inputTokens[4]);
            default -> out.println("Invalid user information! User can't be created!");
        }
    }

    private void handleAddFriend(String[] inputTokens, PrintWriter out) {
        if (!authManager.isAuthenticated()) {
            out.println("You don't have access to this command! You are not authenticated.");
            return;
        }

        String addFriendUsername = inputTokens[1];
        try {
            friendshipService.addFriendship(authManager.getAuthenticatedUser().getUsername(),
                    addFriendUsername);

            notificationService.addNotification(
                    authManager.getAuthenticatedUser().getFullName() +
                            " added you as a friend!",
                    addFriendUsername);

            out.println("Successfully added " + addFriendUsername + " to your friend list!");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleShowFriends(PrintWriter out) {
        if (!authManager.isAuthenticated()) {
            out.println("Invalid command! You are not authenticated.");
            return;
        }

        StringBuilder friendListOutput = new StringBuilder("Friends: ")
                .append(System.lineSeparator());
        try {
            Collection<User> friends = friendshipService
                    .getFriendsForUser(authManager.getAuthenticatedUser().getUsername());

            if (friends.isEmpty()) {
                out.println("No friends to show.");
                return;
            }

            friendListOutput.append(friends
                    .stream()
                    .map(User::getUsername)
                    .map(this::getObligationStatusWithUser)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(System.lineSeparator())));

            out.println(friendListOutput);

        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleCreateGroup(String[] inputTokens, PrintWriter out) throws InvalidCommandInputException {
        if (!authManager.isAuthenticated()) {
            out.println("You don't have access to this command! You are not authenticated.");
            return;
        }

        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException(
                    "Invalid command! Group must have a name and at least two more members!");
        }

        String groupName = inputTokens[1];
        Set<String> usernames = Arrays.stream(inputTokens)
                .skip(2)
                .collect(Collectors.toSet());
        usernames.add(authManager.getAuthenticatedUser().getUsername());

        try {
            for (String username : usernames) {
                if (!username.equals(authManager.getAuthenticatedUser().getUsername())) {
                    notificationService.addNotification(
                            authManager.getAuthenticatedUser().getFullName() +
                                    "added you to group" + groupName + ".",
                            username);
                }
            }
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }

        groupService.addGroup(groupName, usernames);
        out.println("Successfully created group " + groupName + "!");
    }

    private void handleShowGroups(PrintWriter out) {
        if (!authManager.isAuthenticated()) {
            out.println();
            return;
        }

        StringBuilder groupsOutput = new StringBuilder("Groups:")
                .append(System.lineSeparator());

        try {
            Collection<Group> groups = groupService
                    .getGroupsForUser(authManager.getAuthenticatedUser().getUsername());

            if (groups.isEmpty()) {
                out.println("No groups to show.");
                return;
            }

            groupsOutput.append(groups
                    .stream()
                    .map(group -> "* " +
                            group.getName() +
                            System.lineSeparator() +
                            group.getMembers()
                                    .stream()
                                    .map(User::getUsername)
                                    .filter(username -> !username.equals(
                                            authManager.getAuthenticatedUser().getUsername()))
                                    .map(this::getObligationStatusWithUser)
                                    .filter(Objects::nonNull)
                                    .sorted()
                                    .map(str -> "-- " + str)
                                    .collect(Collectors.joining(System.lineSeparator()))
                    )
                    .collect(Collectors.joining(System.lineSeparator())));

            out.println(groupsOutput);

        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleSplitWithFriend(String[] inputTokens, PrintWriter out) throws InvalidCommandInputException {
        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException("Invalid command! " +
                    "Split command must be split <amount> <username> <desc>.");
        }

        double amount = Double.parseDouble(inputTokens[1]);
        String friendUsername = inputTokens[2];
        String description = inputTokens[3];

        try {
            if (!friendshipService.checkFriendship(
                    authManager.getAuthenticatedUser().getUsername(), friendUsername)) {
                out.println("Error! " + friendUsername + " is not your friend!");
            }

            expenseService.addExpense(authManager.getAuthenticatedUser().getUsername(),
                    description,
                    amount,
                    Set.of(friendUsername));

            notificationService.addNotification(
                    String.format("%s paid %s (%s each) for you [%s].",
                            authManager.getAuthenticatedUser().getFullName(),
                            decimalFormat.format(amount),
                            decimalFormat.format(amount / 2),
                            description),
                    friendUsername);

            out.println("You split " + decimalFormat.format(amount) + " with " + friendUsername + ".");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleSplitWithGroup(String[] inputTokens, PrintWriter out)
            throws InvalidCommandInputException {
        if (inputTokens.length < 4) {
            throw new InvalidCommandInputException("Invalid command! " +
                    "Split command must be split-group <amount> <group_name> <desc>.");
        }

        double amount = Double.parseDouble(inputTokens[1]);
        String groupName = inputTokens[2];
        String description = inputTokens[3];

        Optional<Group> group = groupService.findGroupByName(groupName);
        if (group.isEmpty()) {
            out.println("Error! You are not part of a group called " + groupName + "!");
            return;
        }

        Set<String> usernames = group.get().getMembers()
                .stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(authManager.getAuthenticatedUser().getUsername()))
                .collect(Collectors.toSet());

        try {
            expenseService.addExpense(authManager.getAuthenticatedUser().getUsername(),
                    description,
                    amount,
                    usernames);

            notificationService.addNotification(
                    String.format("%s paid %s (%s each) for group %s [%s].",
                            authManager.getAuthenticatedUser().getFullName(),
                            decimalFormat.format(amount),
                            decimalFormat.format(amount / (usernames.size() + 1)),
                            groupName,
                            description),
                    usernames);

            out.println("You split " + decimalFormat.format(amount) + " with group" + groupName + ".");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void handleReceivePayment(String[] inputTokens, PrintWriter out) {
        double amount = Double.parseDouble(inputTokens[1]);
        String payerUsername = inputTokens[2];

        try {
            obligationService.updateObligation(payerUsername,
                    authManager.getAuthenticatedUser().getUsername(),
                    amount);

            notificationService.addNotification(
                    String.format("%s approved your payment of %s.",
                            authManager.getAuthenticatedUser().getFullName(),
                            decimalFormat.format(amount)),
                    payerUsername);

            out.println(payerUsername + " payed you " + decimalFormat.format(amount) + ".");
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private String getObligationStatusWithUser(String username) {
        Optional<User> otherUser = userService.findUserByUsername(username);
        if (otherUser.isEmpty()) {
            return null;
        }

        Optional<Obligation> obligation = obligationService
                .findObligationByUsers(authManager.getAuthenticatedUser(), otherUser.get());

        String obligationStatus = "";
        if (obligation.isPresent()) {
            double balance = obligation.get().getBalance();
            boolean youOwe = (balance > 0 && obligation.get().getSecondUser().equals(otherUser.get()))
                    || (balance < 0 && obligation.get().getFirstUser().equals(otherUser.get()));
            if (balance != 0) {
                obligationStatus = String.format("%s %s",
                        youOwe ? " : You owe" : " : Owes you",
                        decimalFormat.format(Math.abs(balance)));
            }
        }

        return String.format("%s%s", otherUser.get(), obligationStatus);
    }

}
