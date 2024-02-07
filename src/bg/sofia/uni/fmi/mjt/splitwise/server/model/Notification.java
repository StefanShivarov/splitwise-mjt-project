package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification {

    private final String message;
    private final String recipientUsername;
    private final LocalDateTime timestamp;
    private boolean seen;

    public Notification(String message, String recipientUsername) {
        this.message = message;
        this.recipientUsername = recipientUsername;
        this.timestamp = LocalDateTime.now();
        this.seen = false;
    }

    public Notification(String message, String recipientUsername, LocalDateTime timestamp) {
        this.message = message;
        this.recipientUsername = recipientUsername;
        this.timestamp = timestamp;
        this.seen = false;
    }

    public Notification(String message,
                        String recipientUsername,
                        LocalDateTime timestamp,
                        boolean seen) {
        this.message = message;
        this.recipientUsername = recipientUsername;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void markAsSeen() {
        this.seen = true;
    }

    @Override
    public String toString() {
        return String.format("%s - %s",
                getMessage(),
                getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return seen == that.seen
                && Objects.equals(message, that.message)
                && Objects.equals(recipientUsername, that.recipientUsername)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, recipientUsername, timestamp, seen);
    }

}
