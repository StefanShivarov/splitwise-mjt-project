package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.NotificationCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationCsvProcessor notificationCsvProcessorMock;

    @Mock
    private UserService userServiceMock;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        List<Notification> mockData = new ArrayList<>();
        mockData.add(new Notification("message1", "ivan"));
        mockData.add(new Notification("message2", "ivan"));
        mockData.add(new Notification("old-message", "ivan",
                LocalDateTime.of(2022, 12, 7, 2, 2),
                true));
        mockData.add(new Notification("hello", "georgi"));

        when(notificationCsvProcessorMock.loadNotificationsFromCsvFile())
                .thenReturn(mockData);

        notificationService = new NotificationServiceImpl(
                notificationCsvProcessorMock, userServiceMock);
    }

    @Test
    void getNotificationsForUserThrows() {
        when(userServiceMock.findUserByUsername("stamat"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> notificationService.getUnseenNotificationsForUser("stamat"),
                "should throw UserNotFoundException if user is not found!");

        assertThrows(UserNotFoundException.class,
                () -> notificationService.getAllNotificationsForUser("stamat"),
                "should throw UserNotFoundException if user is not found!");
    }

    @Test
    void getAllNotificationsForUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.of(new User("georgi", "0000")));

        assertEquals(3, notificationService.getAllNotificationsForUser("ivan").size(),
                "should return correct collection");
        assertEquals(1, notificationService.getAllNotificationsForUser("georgi").size(),
                "should return correct collection");
    }

    @Test
    void getUnseenNotificationsForUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.of(new User("georgi", "0000")));

        assertEquals(2, notificationService.getUnseenNotificationsForUser("ivan").size(),
                "should return correct collection");
        assertEquals(1, notificationService.getUnseenNotificationsForUser("georgi").size(),
                "should return correct collection");
    }

    @Test
    void testMarkNotificationsAsSeen() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        Collection<Notification> notifications = notificationService
                .getUnseenNotificationsForUser("ivan");

        notificationService.markNotificationsAsSeen(notifications);
        assertEquals(0, notificationService.getUnseenNotificationsForUser("ivan").size(),
                "should clear the unseen notifications");

        verify(notificationCsvProcessorMock, times(1))
                .updateNotificationsInCsvFile(notifications);
    }

    @Test
    void testAddNotificationThrows() {
        when(userServiceMock.findUserByUsername("stamat"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> notificationService.addNotification(null, ""),
                "should throw IllegalArgumentException if arguments are null or blank!");

        assertThrows(UserNotFoundException.class,
                () -> notificationService.addNotification("message", "stamat"),
                "should throw UserNotFoundException if user is not found!");
    }

    @Test
    void testAddNotification() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.of(new User("georgi", "0000")));

        assertEquals(1, notificationService.getAllNotificationsForUser("georgi").size());
        assertDoesNotThrow(() -> notificationService.addNotification("message", Set.of("georgi")));
        assertEquals(2, notificationService.getAllNotificationsForUser("georgi").size(),
                "should add notification to collection!");

        verify(notificationCsvProcessorMock, times(1))
                .writeNotificationToCsvFile(any(Notification.class));
    }

    @Test
    void testGetNotificationsOutput() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("valio"))
                .thenReturn(Optional.of(new User("valio", "1111")));

        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.of(new User("georgi", "0000")));

        assertEquals("No notifications to show.",
                notificationService.getNotificationsOutput(
                        notificationService.getAllNotificationsForUser("valio")),
                "should return appropriate message for empty notifications");

        String expectedNotifications = notificationService.getAllNotificationsForUser("georgi")
                .stream()
                .map(Notification::toString)
                .collect(Collectors.joining(System.lineSeparator()));

        assertEquals("*** Notifications ***" + System.lineSeparator() + expectedNotifications,
                notificationService.getNotificationsOutput(notificationService
                        .getAllNotificationsForUser("georgi")),
                "should return correct notifications output!");
    }

}
