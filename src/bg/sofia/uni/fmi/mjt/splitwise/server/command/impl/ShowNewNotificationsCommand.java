package bg.sofia.uni.fmi.mjt.splitwise.server.command.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.AuthenticationException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.NotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationService;

import java.io.PrintWriter;
import java.util.Collection;

public class ShowNewNotificationsCommand implements Command {

    private final AuthenticationManager authManager;
    private final NotificationService notificationService;

    public ShowNewNotificationsCommand(AuthenticationManager authManager,
                                       NotificationService notificationService) {
        this.authManager = authManager;
        this.notificationService = notificationService;
    }

    @Override
    public void execute(String[] inputTokens, PrintWriter out)
            throws AuthenticationException {
        validate();
        try {
            Collection<Notification> notifications = notificationService
                    .getUnseenNotificationsForUser(
                            authManager.getAuthenticatedUser().getUsername());

            String notificationsOutput = notificationService
                    .getNotificationsOutput(notifications);

            notificationService.markNotificationsAsSeen(notifications);
            out.println(notificationsOutput);
        } catch (UserNotFoundException e) {
            out.println(e.getMessage());
        }
    }

    private void validate() throws NotAuthenticatedException {
        if (!authManager.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

}
