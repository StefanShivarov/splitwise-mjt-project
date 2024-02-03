package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.FriendshipCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendshipServiceImpl implements FriendshipService {

    private final Set<Friendship> friendships;
    private final UserService userService;
    private final FriendshipCsvProcessor friendshipCsvProcessor;

    public FriendshipServiceImpl(UserService userService) {
        this.userService = userService;
        this.friendshipCsvProcessor = new FriendshipCsvProcessor(userService);
        this.friendships = friendshipCsvProcessor.loadFriendshipsFromCsvFile();
    }

    @Override
    public Collection<User> getFriendsForUser(String username) throws UserNotFoundException {
        if (userService.findUserByUsername(username).isEmpty()) {
            throw new UserNotFoundException("User with username " + username + " not found!");
        }

        return friendships
                .stream()
                .filter(friendship -> friendship.getUsers()
                        .stream()
                        .anyMatch(user -> user.getUsername().equals(username)))
                .map(friendship -> friendship.getUsers()
                        .stream()
                        .filter(user -> !user.getUsername().equals(username))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(User::getFullName))
                .toList()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void addFriendship(String firstUsername, String secondUsername) throws UserNotFoundException {
        if (firstUsername == null || firstUsername.isBlank()
                || secondUsername == null || secondUsername.isBlank()) {
            throw new IllegalArgumentException("Invalid arguments! Username is null or blank!");
        }

        Optional<User> firstUser = userService.findUserByUsername(firstUsername);
        Optional<User> secondUser = userService.findUserByUsername(secondUsername);

        if (firstUser.isEmpty()) {
            throw new UserNotFoundException("No user with username " + firstUsername + " exists!");
        }
        if (secondUser.isEmpty()) {
            throw new UserNotFoundException("No user with username " + secondUsername + " exists!");
        }

        Friendship friendship = new Friendship(firstUser.get(), secondUser.get());
        friendships.add(friendship);
        friendshipCsvProcessor.writeFriendshipToCsvFile(friendship);
    }

}
