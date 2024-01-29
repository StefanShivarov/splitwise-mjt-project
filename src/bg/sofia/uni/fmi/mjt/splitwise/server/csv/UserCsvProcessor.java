package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.stream.Collectors;

public class UserCsvProcessor {

    private static final String USERS_CSV_FILE_PATH = "resources/users.csv";

    public static Set<User> loadUsersFromCsvFile() {
        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(new FileInputStream(USERS_CSV_FILE_PATH)))) {

            return csvReader.readAllLines()
                    .stream()
                    .map(UserCsvProcessor::parseFromCsvRow)
                    .collect(Collectors.toSet());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "Error! Could not find the specified file." + USERS_CSV_FILE_PATH, e);
        }
    }

    public static void writeUserToCsvFile(User user) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(USERS_CSV_FILE_PATH), StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(user) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    private static User parseFromCsvRow(String[] rowTokens) {
        int index = 0;
        return new User(
                rowTokens[index++],
                rowTokens[index]
        );
    }

    private static String parseToCsvRow(User user) {
        return String.format("%s,%s",
                user.getUsername(),
                user.getHashedPassword());
    }

}
