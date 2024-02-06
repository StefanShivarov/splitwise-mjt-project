package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpenseCsvProcessor {

    private static final String EXPENSES_CSV_FILE_PATH = "resources/expenses.csv";
    private final UserService userService;

    public ExpenseCsvProcessor(UserService userService) {
        this.userService = userService;
    }

    public List<Expense> loadExpensesFromCsvFile() {
        try (CsvReader csvReader = new CsvReader(
                new InputStreamReader(new FileInputStream(EXPENSES_CSV_FILE_PATH)))) {

            return csvReader.readAllLines()
                    .stream()
                    .map(this::parseFromCsvRow)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeExpenseToCsvFile(Expense expense) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(EXPENSES_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(expense) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    private Expense parseFromCsvRow(String[] stringTokens) {
        if (stringTokens.length < 4) {
            return null;
        }

        String payerUsername = stringTokens[0];
        String desc = stringTokens[1];

        if (payerUsername == null || desc == null
                || payerUsername.isBlank() || desc.isBlank()) {
            return null;
        }

        Optional<User> payer = userService.findUserByUsername(payerUsername);
        if (payer.isEmpty()) {
            return null;
        }

        double amount = Double.parseDouble(stringTokens[2]);
        Set<User> participants = Arrays.stream(stringTokens)
                .skip(3)
                .map(userService::findUserByUsername)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return new Expense(payer.get(), desc, amount, participants);
    }

    private String parseToCsvRow(Expense expense) {
        return String.format("%s,%s,%s,%s",
                expense.payer().getUsername(),
                expense.description(),
                FormatterProvider.getDecimalFormat().format(expense.amount()),
                expense.participants()
                        .stream()
                        .map(User::getUsername)
                        .collect(Collectors.joining(",")));
    }

}
