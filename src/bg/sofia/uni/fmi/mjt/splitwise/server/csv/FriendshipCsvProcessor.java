package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendshipCsvProcessor {

    private static final String FRIENDSHIPS_CSV_FILE_PATH = "resources/friendships.csv";
    private final UserService userService;
    private final CsvReader csvReader;

    public FriendshipCsvProcessor(CsvReader csvReader,
                                  UserService userService) {
        this.csvReader = csvReader;
        this.userService = userService;
    }

    public Set<Friendship> loadFriendshipsFromCsvFile() {
        return csvReader.readAllLines()
                .stream()
                .map(this::parseFromCsvRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void writeFriendshipToCsvFile(Friendship friendship) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(FRIENDSHIPS_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(friendship) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    private Friendship parseFromCsvRow(String[] rowTokens) {
        int index = 0;
        Optional<User> user1 = userService.findUserByUsername(rowTokens[index++]);
        Optional<User> user2 = userService.findUserByUsername(rowTokens[index]);

        if (user1.isEmpty() || user2.isEmpty()) {
            return null;
        }

        return new Friendship(user1.get(), user2.get());
    }

    private String parseToCsvRow(Friendship friendship) {
        return String.format("%s,%s",
                friendship.firstUser().getUsername(),
                friendship.secondUser().getUsername());
    }

}
