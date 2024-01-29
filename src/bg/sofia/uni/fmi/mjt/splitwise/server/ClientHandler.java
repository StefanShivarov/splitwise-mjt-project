package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private static final String UNAUTHENTICATED_MESSAGE = "Please register or login to get started.";

    public ClientHandler(Socket socket,
                         AuthenticationManager authManager,
                         UserService userService) {
        this.socket = socket;
        this.authManager = authManager;
        this.userService = userService;
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
            default:
                out.println("Invalid command! Try again!");
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

}
