package bg.sofia.uni.fmi.mjt.splitwise.server.security;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;

import java.util.Optional;

public class AuthenticationManager {

    private final UserService userService;
    private User currentUser;

    public AuthenticationManager(UserService userService) {
        this.userService = userService;
        currentUser = null;
    }

    public boolean authenticate(String username, String pass) {
        Optional<User> user = userService.findUserByUsername(username);

        if (user.isPresent()
                && user.get().getHashedPassword()
                .equals(PasswordHasher.hashPassword(pass))) {

            currentUser = user.get();
            return true;
        }

        return false;
    }

    public User getAuthenticatedUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }

}
