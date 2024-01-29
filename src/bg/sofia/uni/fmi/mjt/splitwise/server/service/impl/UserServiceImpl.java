package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.io.parser.UserParser;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {

    private final Set<User> users;

    public UserServiceImpl() {
        this.users = UserParser.parseUsersFromCsvFile();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return users
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findUserById(long id) {
        return users
                .stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

}
