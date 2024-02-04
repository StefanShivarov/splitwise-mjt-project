package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ExpenseServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.FriendshipServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.GroupServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ObligationServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.UserServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitwiseServer {

    private static final int SERVER_PORT = 7777;
    private static final int EXECUTORS_AMOUNT = 10;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(EXECUTORS_AMOUNT);
        Thread.currentThread().setName("Splitwise Server Thread");

        UserService userService = new UserServiceImpl();
        FriendshipService friendshipService = new FriendshipServiceImpl(userService);
        GroupService groupService = new GroupServiceImpl(userService);
        ObligationService obligationService = new ObligationServiceImpl(userService);
        ExpenseService expenseService = new ExpenseServiceImpl(userService, obligationService);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("New client connected.");
                executor.execute(new ClientHandler(clientSocket,
                        new AuthenticationManager(userService),
                        userService,
                        friendshipService,
                        groupService,
                        expenseService,
                        obligationService));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred with the server socket!", e);
        } finally {
            executor.shutdown();
        }
    }
}
