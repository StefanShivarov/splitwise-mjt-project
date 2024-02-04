package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.GroupCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService {

    private final UserService userService;
    private final GroupCsvProcessor groupCsvProcessor;
    private final Set<Group> groups;

    public GroupServiceImpl(UserService userService) {
        this.userService = userService;
        this.groupCsvProcessor = new GroupCsvProcessor(userService);
        this.groups = groupCsvProcessor.loadGroupsFromCsvFile();
    }

    @Override
    public Collection<Group> getGroupsForUser(String username) throws UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with username + " + username + " was not found!");
        }

        return groups
                .stream()
                .filter(group -> group.getMembers().contains(user.get()))
                .sorted(Comparator.comparing(Group::getName))
                .toList()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void addGroup(String name, Set<String> usernames) {
        if (name == null || name.isBlank() || usernames == null) {
            throw new IllegalArgumentException("Invalid arguments! Can't add group to database!");
        }

        Set<User> users = usernames
                .stream()
                .map(userService::findUserByUsername)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Group group = new Group(name, users);
        groups.add(group);
        groupCsvProcessor.writeGroupToCsvFile(group);
    }

    @Override
    public Optional<Group> findGroupByName(String name) {
        return groups
                .stream()
                .filter(group -> group.getName().equals(name))
                .findFirst();
    }

}
