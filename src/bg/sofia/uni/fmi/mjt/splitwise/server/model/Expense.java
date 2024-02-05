package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public record Expense(User payer, String description, double amount, Set<User> participants) {

    public Set<User> participants() {
        return Collections.unmodifiableSet(participants);
    }

    @Override
    public String toString() {
        return String.format("%.2f for %s; Reason: [%s]",
                amount,
                participants
                        .stream()
                        .map(User::getUsername)
                        .collect(Collectors.joining(",")),
                description);
    }

}
