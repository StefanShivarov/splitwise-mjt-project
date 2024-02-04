package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Group {

    private final String name;
    private final Set<User> members;

    public Group(String name, Set<User> users) {
        this.name = name;
        this.members = users;
    }

    public String getName() {
        return name;
    }

    public Collection<User> getMembers() {
        return Collections.unmodifiableSet(members);
    }

}
