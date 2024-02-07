package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name) && Objects.equals(members, group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members);
    }

}
