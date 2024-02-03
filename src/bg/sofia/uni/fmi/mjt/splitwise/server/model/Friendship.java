package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Set;

public record Friendship(User firstUser, User secondUser) {

    public Collection<User> getUsers() {
        return Set.of(firstUser, secondUser);
    }

}
