package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.csv.CsvProcessorInitializer;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ExpenseServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.FriendshipServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.GroupServiceImpl;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.NotificationServiceImpl;
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

        UserService userService = new UserServiceImpl(
                CsvProcessorInitializer.newUserCsvProcessor());
        FriendshipService friendshipService = new FriendshipServiceImpl(
                CsvProcessorInitializer.newFriendshipCsvProcessor(userService), userService);
        GroupService groupService = new GroupServiceImpl(
                CsvProcessorInitializer.newGroupCsvProcessor(userService), userService);
        ObligationService obligationService = new ObligationServiceImpl(
                CsvProcessorInitializer.newObligationCsvProcessor(userService), userService);
        ExpenseService expenseService = new ExpenseServiceImpl(
                CsvProcessorInitializer.newExpenseCsvProcessor(userService),
                userService, obligationService);
        NotificationService notificationService = new NotificationServiceImpl(
                CsvProcessorInitializer.newNotificationCsvProcessor(), userService);

        runServer(executor, userService, friendshipService, groupService,
                expenseService, obligationService, notificationService);
    }

    private static void runServer(ExecutorService executor, UserService userService,
                           FriendshipService friendshipService, GroupService groupService,
                           ExpenseService expenseService, ObligationService obligationService,
                           NotificationService notificationService) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket,
                        new CommandFactory(
                                new AuthenticationManager(userService), userService,
                                friendshipService, groupService, expenseService,
                                obligationService, notificationService)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred with the server socket!", e);
        } finally {
            executor.shutdown();
        }
    }

}
