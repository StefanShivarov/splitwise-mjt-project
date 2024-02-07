package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import bg.sofia.uni.fmi.mjt.splitwise.server.util.FormatterProvider;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record Expense(User payer, String description, double amount, Set<User> participants) {

    public Set<User> participants() {
        return Collections.unmodifiableSet(participants);
    }

    @Override
    public String toString() {
        return String.format("%s for %s; Reason: [%s]",
                FormatterProvider.getDecimalFormat().format(amount),
                participants
                        .stream()
                        .map(User::getUsername)
                        .collect(Collectors.joining(",")),
                description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(amount, expense.amount) == 0
                && Objects.equals(payer, expense.payer)
                && Objects.equals(description, expense.description)
                && Objects.equals(participants, expense.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payer, description, amount, participants);
    }

}
