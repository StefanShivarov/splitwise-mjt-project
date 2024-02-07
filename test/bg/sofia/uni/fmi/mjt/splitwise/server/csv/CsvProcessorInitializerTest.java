package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CsvProcessorInitializerTest {

    @Mock
    private UserService userServiceMock;

    @Test
    void testInitializesCorrectCsvProcessor() {
        assertInstanceOf(UserCsvProcessor.class,
                CsvProcessorInitializer.newUserCsvProcessor(),
                "should return correct csv processor!");
        assertInstanceOf(FriendshipCsvProcessor.class,
                CsvProcessorInitializer.newFriendshipCsvProcessor(userServiceMock),
                "should return correct csv processor!");
        assertInstanceOf(GroupCsvProcessor.class,
                CsvProcessorInitializer.newGroupCsvProcessor(userServiceMock),
                "should return correct csv processor!");
        assertInstanceOf(ExpenseCsvProcessor.class,
                CsvProcessorInitializer.newExpenseCsvProcessor(userServiceMock),
                "should return correct csv processor!");
        assertInstanceOf(ObligationCsvProcessor.class,
                CsvProcessorInitializer.newObligationCsvProcessor(userServiceMock),
                "should return correct csv processor!");
        assertInstanceOf(NotificationCsvProcessor.class,
                CsvProcessorInitializer.newNotificationCsvProcessor(),
                "should return correct csv processor!");
    }

}
