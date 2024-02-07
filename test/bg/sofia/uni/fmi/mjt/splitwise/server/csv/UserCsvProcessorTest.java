package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.util.MockDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCsvProcessorTest {

    @Mock
    private CsvReader csvReaderMock;

    @InjectMocks
    private UserCsvProcessor userCsvProcessor;

    @BeforeEach
    void setUp() {
        userCsvProcessor = new UserCsvProcessor(csvReaderMock);

        when(csvReaderMock.readAllLines())
                .thenReturn(MockDatabase.USERS_CSV.lines()
                        .map(line -> line.split(","))
                        .toList());
    }

    @Test
    void testLoadUsersFromCsvFile() {
        Set<String> loadedUsers = userCsvProcessor.loadUsersFromCsvFile()
                .stream()
                .map(User::toString)
                .collect(Collectors.toSet());

        assertEquals(3, loadedUsers.size(),
                "loadUsersFromCsvFile should load correct data!");
        assertTrue(loadedUsers.contains("Stefan Shivarov (stefanaki95)"),
                "loadUsersFromCsvFile should load correct data!");
        assertTrue(loadedUsers.contains("Ivan Ivanov (ivan)"),
                "loadUsersFromCsvFile should load correct data!");
        assertTrue(loadedUsers.contains("valio"),
                "loadUsersFromCsvFile should load correct data!");
    }

}
