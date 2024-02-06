package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GroupService {

    Collection<Group> getGroupsForUser(String username)
            throws UserNotFoundException;

    void addGroup(String name, Set<String> usernames);

    Optional<Group> findGroupByName(String name);

}
