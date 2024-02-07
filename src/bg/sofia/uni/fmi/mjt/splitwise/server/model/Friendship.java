package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public record Friendship(User firstUser, User secondUser) {

    public Collection<User> getUsers() {
        return Set.of(firstUser, secondUser);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(firstUser, that.firstUser)
                && Objects.equals(secondUser, that.secondUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser);
    }

}
