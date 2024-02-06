package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.NotificationCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements NotificationService {

    private final UserService userService;
    private final NotificationCsvProcessor notificationCsvProcessor;
    private final List<Notification> notifications;

    public NotificationServiceImpl(UserService userService) {
        this.userService = userService;
        this.notificationCsvProcessor = new NotificationCsvProcessor();
        this.notifications = notificationCsvProcessor.loadNotificationsFromCsvFile();
    }

    @Override
    public Collection<Notification> getAllNotificationsForUser(String username) throws UserNotFoundException {
        if (userService.findUserByUsername(username).isEmpty()) {
            throw new UserNotFoundException("User with username " +
                    username + " was not found!");
        }

        return notifications
                .stream()
                .filter(notification -> notification.getRecipientUsername().equals(username))
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .toList();
    }

    @Override
    public Collection<Notification> getUnseenNotificationsForUser(String username) throws UserNotFoundException {
        if (userService.findUserByUsername(username).isEmpty()) {
            throw new UserNotFoundException("User with username " +
                    username + " was not found!");
        }

        return notifications
                .stream()
                .filter(notification -> notification.getRecipientUsername().equals(username))
                .filter(notification -> !notification.isSeen())
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .toList();
    }

    @Override
    public String getNotificationsOutput(Collection<Notification> notifications) {
        if (notifications.isEmpty()) {
            return "No notifications to show.";
        }

        return "*** Notifications ***" + System.lineSeparator() +
                notifications
                        .stream()
                        .map(Notification::toString)
                        .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public void markNotificationsAsSeen(Collection<Notification> notifications) {
        notifications.forEach(Notification::markAsSeen);
        notificationCsvProcessor.updateNotificationsInCsvFile(notifications);
    }

    @Override
    public void addNotification(String message, String recipientUsername) throws UserNotFoundException {
        if (message == null || recipientUsername == null
                || message.isBlank() || recipientUsername.isBlank()) {
            throw new IllegalArgumentException("Invalid arguments! " +
                    "Message or recipient username is blank or null!");
        }

        if (userService.findUserByUsername(recipientUsername).isEmpty()) {
            throw new UserNotFoundException("User with username " +
                    recipientUsername + " was not found!");
        }

        Notification notification = new Notification(message, recipientUsername);
        notifications.add(notification);
        notificationCsvProcessor.writeNotificationToCsvFile(notification);
    }

    @Override
    public void addNotification(String message, Collection<String> recipientsUsernames) throws UserNotFoundException {
        for (String username : recipientsUsernames) {
            addNotification(message, username);
        }
    }

}
