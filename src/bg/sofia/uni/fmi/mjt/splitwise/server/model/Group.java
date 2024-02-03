package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Group {

    private final String name;
    private final Set<User> users;

    public Group(String name, Set<User> users) {
        this.name = name;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public Collection<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    public boolean addUser(User user) {
        return users.add(user);
    }

}
