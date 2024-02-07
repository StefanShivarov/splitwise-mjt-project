package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvProcessorInitializer {

    private static final Path USERS_CSV_FILE_PATH =
            Path.of("resources/users.csv");
    private static final Path FRIENDSHIPS_CSV_FILE_PATH =
            Path.of("resources/friendships.csv");
    private static final Path GROUPS_CSV_FILE_PATH =
            Path.of("resources/groups.csv");
    private static final Path OBLIGATIONS_CSV_FILE_PATH =
            Path.of("resources/obligations.csv");
    private static final Path EXPENSES_CSV_FILE_PATH =
            Path.of("resources/expenses.csv");
    private static final Path NOTIFICATIONS_CSV_FILE_PATH =
            Path.of("resources/notifications.csv");

    public static UserCsvProcessor newUserCsvProcessor() {
        try {
            CsvReader csvReader = new CsvReader(Files.newBufferedReader(USERS_CSV_FILE_PATH));
            return new UserCsvProcessor(csvReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FriendshipCsvProcessor newFriendshipCsvProcessor(UserService userService) {
        try {
            CsvReader csvReader = new CsvReader(
                    Files.newBufferedReader(FRIENDSHIPS_CSV_FILE_PATH));
            return new FriendshipCsvProcessor(csvReader, userService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GroupCsvProcessor newGroupCsvProcessor(UserService userService) {
        try {
            CsvReader csvReader = new CsvReader(
                    Files.newBufferedReader(GROUPS_CSV_FILE_PATH));
            return new GroupCsvProcessor(csvReader, userService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObligationCsvProcessor newObligationCsvProcessor(UserService userService) {
        try {
            CsvReader csvReader = new CsvReader(
                    Files.newBufferedReader(OBLIGATIONS_CSV_FILE_PATH));
            return new ObligationCsvProcessor(csvReader, userService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExpenseCsvProcessor newExpenseCsvProcessor(UserService userService) {
        try {
            CsvReader csvReader = new CsvReader(
                    Files.newBufferedReader(EXPENSES_CSV_FILE_PATH));
            return new ExpenseCsvProcessor(csvReader, userService);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NotificationCsvProcessor newNotificationCsvProcessor() {
        try {
            CsvReader csvReader = new CsvReader(
                    Files.newBufferedReader(NOTIFICATIONS_CSV_FILE_PATH));
            return new NotificationCsvProcessor(csvReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
