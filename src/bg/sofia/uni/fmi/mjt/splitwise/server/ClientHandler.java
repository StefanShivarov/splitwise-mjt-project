package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final GroupService groupService;
    private static final String UNAUTHENTICATED_MESSAGE = "Please register or login to get started.";
    private static final String CLIENT_WELCOME_MESSAGE = "---------Welcome to Splitwise!---------";

    public ClientHandler(Socket socket,
                         AuthenticationManager authManager,
                         UserService userService,
                         FriendshipService friendshipService,
                         GroupService groupService) {
        this.socket = socket;
        this.authManager = authManager;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.groupService = groupService;
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
                case "login" -> handleLogin(inputTokens, out);
                case "logout" -> handleLogout(out);
                case "register" -> handleRegister(inputTokens, out);
                case "add-friend" -> handleAddFriend(inputTokens, out);
                case "create-group" -> handleCreateGroup(inputTokens, out);
                case "my-friends" -> handleShowFriends(out);
                case "my-groups" -> handleShowGroups(out);
                default -> out.println("Invalid command! Try again!");
            }
        } catch (InvalidCommandInputException e) {
            out.println(e.getMessage());
        }

    }

    private void handleLogin(String[] inputTokens, PrintWriter out) {
        if (authManager.isAuthenticated()) {
            out.println("You don't have access to this command! You are already logged in.");
            return;
        }

        if (authManager.authenticate(inputTokens[1], inputTokens[2])) {
            out.println("Welcome, " + authManager.getAuthenticatedUser().getUsername() + "!");
        } else {
            out.println("Invalid credentials! Login unsuccessful!");
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
                    .map(User::getFullName)
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
                    .map(group -> "- " +
                            group.getName() +
                            System.lineSeparator() +
                            group.getUsers()
                                    .stream()
                                    .map(user -> "-- " + user.getUsername())
                                    .sorted()
                                    .collect(Collectors.joining(System.lineSeparator()))
                    )
                    .collect(Collectors.joining(System.lineSeparator())));

            out.println(groupsOutput);

        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }


}
