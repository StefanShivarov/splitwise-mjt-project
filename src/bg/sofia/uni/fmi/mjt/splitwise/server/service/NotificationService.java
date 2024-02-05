package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;

import java.util.Collection;

public interface NotificationService {

    Collection<Notification> getAllNotificationsForUser(String username) throws UserNotFoundException;

    Collection<Notification> getUnseenNotificationsForUser(String username) throws UserNotFoundException;

    void markNotificationsAsSeen(Collection<Notification> notifications);

    void addNotification(String message, String recipientUsername) throws UserNotFoundException;

    void addNotification(String message, Collection<String> recipientsUsernames) throws UserNotFoundException;

}
