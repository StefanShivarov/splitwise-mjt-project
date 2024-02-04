package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collections;
import java.util.Set;

public record Expense(User payer, String description, double amount, Set<User> participants) {

    public Set<User> participants() {
        return Collections.unmodifiableSet(participants);
    }

}
