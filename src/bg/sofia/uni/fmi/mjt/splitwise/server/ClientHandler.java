package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthenticationManager authManager;
    private static final String UNAUTHENTICATED_MESSAGE = "Please register or login to get started.";

    public ClientHandler(Socket socket, AuthenticationManager authManager) {
        this.socket = socket;
        this.authManager = authManager;
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
        if (authManager.logout()) {
            out.println("Logout successful!");
        } else {
            out.println("Logout failed: You are not currently logged in." +
                    " Please log in first before attempting to logout.");
        }
    }

}
