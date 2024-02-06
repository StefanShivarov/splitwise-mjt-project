package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationCsvProcessor {

    private static final String NOTIFICATIONS_CSV_FILE_PATH = "resources/notifications.csv";

    public NotificationCsvProcessor() {

    }

    public List<Notification> loadNotificationsFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(
                new InputStreamReader(new FileInputStream(NOTIFICATIONS_CSV_FILE_PATH)))) {

            return csvReader.readAllLines()
                    .stream()
                    .map(this::parseFromCsvRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeNotificationToCsvFile(Notification notification) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(NOTIFICATIONS_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(notification) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    public void updateNotificationsInCsvFile(Collection<Notification> updatedNotifications) {
        try (CsvReader csvReader = new CsvReader(
                new InputStreamReader(new FileInputStream(NOTIFICATIONS_CSV_FILE_PATH)))) {

            List<String> csvLines = csvReader.readAllLinesRaw();
            updatedNotifications.forEach(updatedNotification -> {
                    csvLines.removeIf(line -> lineMatchesNotification(line, updatedNotification));
                    csvLines.add(parseToCsvRow(updatedNotification));
            });

            try(var bufferedWriter = Files.newBufferedWriter(Path.of(NOTIFICATIONS_CSV_FILE_PATH))) {
                bufferedWriter.write("");

                for (String line : csvLines) {
                    bufferedWriter.write(line + System.lineSeparator());
                }

                bufferedWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException("Error while writing to file!", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while updating obligation in database!", e);
        }
    }

    private boolean lineMatchesNotification(String csvLine, Notification notification) {
        return csvLine.startsWith(notification.getMessage() +
                "," +
                notification.getRecipientUsername() +
                "," +
                notification.getTimestamp().format(FormatterProvider.getDateTimeFormatter()));
    }

    private Notification parseFromCsvRow(String[] stringTokens) {
        String message = stringTokens[0];
        String username = stringTokens[1];
        if (message == null || username == null
                || message.isBlank() || username.isBlank()) {
            return null;
        }

        LocalDateTime timestamp = LocalDateTime.parse(stringTokens[2],
                FormatterProvider.getDateTimeFormatter());
        boolean isSeen = Boolean.parseBoolean(stringTokens[3]);

        return new Notification(message, username, timestamp, isSeen);
    }

    private String parseToCsvRow(Notification notification) {
        return String.format("%s,%s,%s,%s",
                notification.getMessage(),
                notification.getRecipientUsername(),
                notification.getTimestamp()
                        .format(FormatterProvider.getDateTimeFormatter()),
                notification.isSeen());
    }

}
