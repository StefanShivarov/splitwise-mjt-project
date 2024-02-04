package bg.sofia.uni.fmi.mjt.splitwise.server.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Group {

    private final String name;
    private final Set<User> participants;

    public Group(String name, Set<User> users) {
        this.name = name;
        this.participants = users;
    }

    public String getName() {
        return name;
    }

    public Collection<User> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    public boolean addParticipant(User user) {
        return participants.add(user);
    }

}
