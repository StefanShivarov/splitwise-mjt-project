package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupCsvProcessor {

    private static final String GROUPS_CSV_FILE_PATH = "resources/groups.csv";
    private static final int GROUP_NAME_INDEX = 0;
    private static final int SKIP_TO_USERNAMES = 1;
    private final UserService userService;
    private final CsvReader csvReader;

    public GroupCsvProcessor(CsvReader csvReader,
                             UserService userService) {
        this.csvReader = csvReader;
        this.userService = userService;
    }

    public Set<Group> loadGroupsFromCsvFile() {
        return csvReader.readAllLines()
                .stream()
                .map(this::parseFromCsvRow)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void writeGroupToCsvFile(Group group) {
        try (var bufferedWriter = Files.newBufferedWriter(Path.of(GROUPS_CSV_FILE_PATH),
                StandardOpenOption.APPEND)) {
            bufferedWriter.write(parseToCsvRow(group) + System.lineSeparator());
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing to file!", e);
        }
    }

    private Group parseFromCsvRow(String[] stringTokens) {
        String groupName = stringTokens[GROUP_NAME_INDEX];
        if (groupName == null || groupName.isBlank()) {
            return null;
        }

        Set<User> groupParticipants = Arrays.stream(stringTokens)
                .skip(SKIP_TO_USERNAMES)
                .map(userService::findUserByUsername)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return new Group(groupName, groupParticipants);
    }

    private String parseToCsvRow(Group group) {
        return String.format("%s,%s",
                group.getName(),
                group.getMembers()
                        .stream().map(User::getUsername)
                        .collect(Collectors.joining(",")));
    }

}
