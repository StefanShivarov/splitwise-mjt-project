package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;
import bg.sofia.uni.fmi.mjt.splitwise.util.MockDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationCsvProcessorTest {

    @Mock
    private CsvReader csvReaderMock;

    @InjectMocks
    private NotificationCsvProcessor notificationCsvProcessor;

    @BeforeEach
    void setUp() {
        when(csvReaderMock.readAllLines())
                .thenReturn(MockDatabase.NOTIFICATIONS_CSV.lines()
                        .map(line -> line.split(","))
                        .toList());

        notificationCsvProcessor = new NotificationCsvProcessor(csvReaderMock);
    }

    @Test
    void testLoadNotificationsFromCsvFile() {
        List<Notification> notifications = notificationCsvProcessor
                .loadNotificationsFromCsvFile();

        assertEquals(4, notifications.size(),
                "should load correct collection!");
        assertTrue(notifications.contains(
                new Notification(
                        "stefan shivarov paid 20.00 (10.00 each) for you [vodka].",
                        "ivan",
                        LocalDateTime.parse("2024-02-06 21:30:42",
                                FormatterProvider.getDateTimeFormatter()),
                        true)),
                "should load correct collection!"
        );
    }
}
