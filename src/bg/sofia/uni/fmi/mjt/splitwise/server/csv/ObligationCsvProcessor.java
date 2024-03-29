package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.ObligationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Obligation;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ObligationCsvProcessor {

    private static final String OBLIGATIONS_CSV_FILE_PATH = "resources/obligations.csv";
    private static final int FIRST_USERNAME_INDEX = 0;
    private static final int SECOND_USERNAME_INDEX = 1;
    private static final int BALANCE_INDEX = 2;
    private final UserService userService;
    private final CsvReader csvReader;

    public ObligationCsvProcessor(CsvReader csvReader,
                                  UserService userService) {
        this.userService = userService;
        this.csvReader = csvReader;
    }

    public Set<Obligation> loadObligationsFromCsvFile() {
        return csvReader.readAllLines()
                .stream()
                .map(this::parseFromCsvRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void writeObligationToCsvFile(Obligation obligation) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(OBLIGATIONS_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(obligation) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    public void updateObligationInCsvFile(Obligation updatedObligation)
            throws ObligationNotFoundException {
        try (CsvReader csvReader = new CsvReader(
                new InputStreamReader(new FileInputStream(OBLIGATIONS_CSV_FILE_PATH)))) {
            List<String> csvLines = csvReader.readAllLinesRaw();
            OptionalInt lineIndex = IntStream.range(0, csvLines.size())
                    .filter(index ->
                            lineMatchesObligationUsernames(csvLines.get(index), updatedObligation))
                    .findFirst();

            if (lineIndex.isEmpty()) {
                throw new ObligationNotFoundException(
                        "Error! Can't update obligation that is not in database!");
            }
            csvLines.remove(lineIndex.getAsInt());
            csvLines.add(parseToCsvRow(updatedObligation));

            try (var bufferedWriter = Files.newBufferedWriter(
                    Path.of(OBLIGATIONS_CSV_FILE_PATH))) {
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

    private boolean lineMatchesObligationUsernames(String csvLine, Obligation obligation) {
        return csvLine.startsWith(obligation.getFirstUser().getUsername()
                + ","
                + obligation.getSecondUser().getUsername());
    }

    private Obligation parseFromCsvRow(String[] stringTokens) {
        String firstUsername = stringTokens[FIRST_USERNAME_INDEX];
        String secondUsername = stringTokens[SECOND_USERNAME_INDEX];
        if (firstUsername == null || secondUsername == null
                || firstUsername.isBlank() || secondUsername.isBlank()) {
            return null;
        }

        Optional<User> firstUser = userService.findUserByUsername(firstUsername);
        Optional<User> secondUser = userService.findUserByUsername(secondUsername);
        if (firstUser.isEmpty() || secondUser.isEmpty()) {
            return null;
        }

        double balance = Double.parseDouble(stringTokens[BALANCE_INDEX]);

        return new Obligation(firstUser.get(), secondUser.get(), balance);
    }

    private String parseToCsvRow(Obligation obligation) {
        return String.format("%s,%s,%s",
                obligation.getFirstUser().getUsername(),
                obligation.getSecondUser().getUsername(),
                FormatterProvider.getDecimalFormat()
                        .format(obligation.getBalance()));
    }

}
