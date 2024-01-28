package bg.sofia.uni.fmi.mjt.splitwise.io.parser;

import bg.sofia.uni.fmi.mjt.splitwise.io.CSVReader;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class UserParser {

    private static final String USERS_CSV_FILE_PATH = "resources/users.csv";

    public static Set<User> parseUsersFromCsvFile() {
        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(new FileInputStream(USERS_CSV_FILE_PATH)))) {

            return csvReader.readAllLines()
                    .stream()
                    .map(UserParser::parseFromCsvRow)
                    .collect(Collectors.toSet());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "Error! Could not find the specified file." + USERS_CSV_FILE_PATH, e);
        }
    }

    private static User parseFromCsvRow(String[] rowTokens) {
        int index = 0;
        return new User(
                Long.parseLong(rowTokens[index++]),
                rowTokens[index++],
                rowTokens[index]
        );
    }

}
