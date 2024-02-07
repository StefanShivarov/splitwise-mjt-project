package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.stream.Collectors;

public class UserCsvProcessor {

    private static final String USERS_CSV_FILE_PATH = "resources/users.csv";
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private static final int FIRST_NAME_INDEX = 2;
    private static final int LAST_NAME_INDEX = 3;
    private final CsvReader csvReader;

    public UserCsvProcessor(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    public Set<User> loadUsersFromCsvFile() {
        return csvReader.readAllLines()
                .stream()
                .map(this::parseFromCsvRow)
                .collect(Collectors.toSet());
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
        User user = new User(rowTokens[USERNAME_INDEX], rowTokens[PASSWORD_INDEX]);

        if (rowTokens.length > FIRST_NAME_INDEX) {
            user.setFirstName(rowTokens[FIRST_NAME_INDEX]);
        }
        if (rowTokens.length > LAST_NAME_INDEX) {
            user.setLastName(rowTokens[LAST_NAME_INDEX]);
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
