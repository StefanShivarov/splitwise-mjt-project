package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import bg.sofia.uni.fmi.mjt.splitwise.util.MockDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendshipCsvProcessorTest {

    @Mock
    private CsvReader csvReaderMock;

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private FriendshipCsvProcessor friendshipCsvProcessor;

    @BeforeEach
    void setUp() {
        friendshipCsvProcessor = new FriendshipCsvProcessor(csvReaderMock, userServiceMock);

        when(csvReaderMock.readAllLines())
                .thenReturn(MockDatabase.FRIENDSHIPS_CSV.lines()
                        .map(line -> line.split(","))
                        .toList());
    }

    @Test
    void testLoadFriendshipsFromCsvFile() {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        when(userServiceMock.findUserByUsername("stefanaki95"))
                .thenReturn(Optional.of(new User("stefanaki95", "1111")));
        when(userServiceMock.findUserByUsername("valio"))
                .thenReturn(Optional.of(new User("valio", "0000")));

        Friendship friendship1 = new Friendship(
                new User("ivan", "12345"),
                new User("stefanaki95", "1111")
        );

        Friendship friendship2 = new Friendship(
                new User("valio", "0000"),
                new User("stefanaki95", "1111")
        );

        Set<Friendship> actual = friendshipCsvProcessor.loadFriendshipsFromCsvFile();
        assertEquals(2, actual.size(),
                "should return correct collection!");
        assertTrue(actual.contains(friendship1),
                "should return correct collection!");
        assertTrue(actual.contains(friendship2),
                "should return correct collection!");
    }

}
