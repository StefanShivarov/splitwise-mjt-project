package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Set;

public class Friendship {

    private final User firstUser;
    private final User secondUser;

    public Friendship(User first, User second) {
        firstUser = first;
        secondUser = second;
    }

    public Collection<User> getUsers() {
        return Set.of(firstUser, secondUser);
    }

    public User getFirstUser() {
        return firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

}
