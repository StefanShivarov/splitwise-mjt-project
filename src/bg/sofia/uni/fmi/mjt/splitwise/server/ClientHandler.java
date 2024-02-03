package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private static final String UNAUTHENTICATED_MESSAGE = "Please register or login to get started.";

    public ClientHandler(Socket socket,
                         AuthenticationManager authManager,
                         UserService userService,
                         FriendshipService friendshipService) {
        this.socket = socket;
        this.authManager = authManager;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Handler " + socket.getRemoteSocketAddress());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

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
        switch (inputTokens[0]) {
            case "login":
                handleLogin(inputTokens, out);
                break;
            case "logout":
                handleLogout(out);
                break;
            case "register":
                handleRegister(inputTokens, out);
                break;
            case "add-friend":
                handleAddFriend(inputTokens, out);
                break;
            case "my-friends":
                handleShowFriends(out);
                break;
            default:
                out.println("Invalid command!oo Try again!");
                break;
        }
    }

    private void handleLogin(String[] inputTokens, PrintWriter out) {
        if (authManager.isAuthenticated()) {
            out.println("Invalid command! You are already logged in.");
            return;
        }

        if (authManager.authenticate(inputTokens[1], inputTokens[2])) {
            out.println("Welcome, " + authManager.getAuthenticatedUser().getUsername());
        } else {
            out.println("Invalid credentials!");
        }
    }

    private void handleLogout(PrintWriter out) {
        if (!authManager.isAuthenticated()) {
            out.println("Logout failed: You are not currently logged in." +
                    " Please log in first before attempting to logout.");
            return;
        }

        authManager.logout();
    }

    private void handleRegister(String[] inputTokens, PrintWriter out) {
        if (authManager.isAuthenticated()) {
            out.println("Invalid command! You are already logged in.");
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
            out.println("Invalid command! You are not authenticated.");
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


}
