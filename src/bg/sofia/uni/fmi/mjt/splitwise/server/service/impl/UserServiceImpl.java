package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.UserCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.PasswordHasher;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {

    private final Set<User> users;
    private final UserCsvProcessor userCsvProcessor;

    public UserServiceImpl() {
        this.userCsvProcessor = new UserCsvProcessor();
        this.users = userCsvProcessor.loadUsersFromCsvFile();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return users
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public void addUser(String username, String pass, String firstName, String lastName) {
        if (username == null || pass == null || username.isBlank() || pass.isBlank()) {
            throw new IllegalArgumentException("Invalid user credentials! User can't be created!");
        }

        User user = new User(username, PasswordHasher.hashPassword(pass), firstName, lastName);
        users.add(user);
        userCsvProcessor.writeUserToCsvFile(user);
    }

}
