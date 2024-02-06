package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.InvalidCommandInputException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CommandFactory commandFactory;
    private static final String PLEASE_LOGIN_OR_REGISTER_MESSAGE =
            "Please register or login to get started.";
    private static final String NOT_AUTHENTICATED_MESSAGE =
            "You don't have access to this command! "
                    + "You are not authenticated.";
    private static final String CLIENT_WELCOME_MESSAGE =
            "---------Welcome to Splitwise!---------";
    private static final int COMMAND_INDEX = 0;

    public ClientHandler(Socket socket,
                         CommandFactory commandFactory) {
        this.socket = socket;
        this.commandFactory = commandFactory;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Handler " + socket.getRemoteSocketAddress());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(CLIENT_WELCOME_MESSAGE);
            out.println(PLEASE_LOGIN_OR_REGISTER_MESSAGE);

            String input;
            while ((input = in.readLine()) != null) {
                executeCommand(input, out);
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

    private void executeCommand(String input, PrintWriter out) {
        String[] inputTokens = input.split("\\s+");
        try {
            Command command = commandFactory.createCommand(inputTokens[COMMAND_INDEX]);
            command.execute(inputTokens, out);
        } catch (NotAuthenticatedException e) {
            out.println(NOT_AUTHENTICATED_MESSAGE
                    + System.lineSeparator()
                    + PLEASE_LOGIN_OR_REGISTER_MESSAGE);
        } catch (InvalidCommandInputException | AuthenticationException e) {
            out.println(e.getMessage());
        }
    }

}
