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

    public Set<User> loadUsersFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(
                new InputStreamReader(new FileInputStream(USERS_CSV_FILE_PATH)))) {

            return csvReader.readAllLines()
                    .stream()
                    .map(this::parseFromCsvRow)
                    .collect(Collectors.toSet());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "Error! Could not find the specified file." + USERS_CSV_FILE_PATH, e);
        }
    }

    public void writeUserToCsvFile(User user) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(USERS_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(user) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    private User parseFromCsvRow(String[] rowTokens) {
        int index = 0;
        User user = new User(rowTokens[index++], rowTokens[index++]);

        if (rowTokens.length > 2) {
            user.setFirstName(rowTokens[index++]);
        }
        if (rowTokens.length > 3) {
            user.setLastName(rowTokens[index]);
        }

        return user;
    }

    private String parseToCsvRow(User user) {
        return String.format("%s,%s,%s,%s",
                user.getUsername(),
                user.getHashedPassword(),
                user.getFirstName(),
                user.getLastName());
    }

}
