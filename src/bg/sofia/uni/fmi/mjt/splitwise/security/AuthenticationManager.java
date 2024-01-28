package bg.sofia.uni.fmi.mjt.splitwise.security;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.service.UserService;

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
        return user.isPresent()
                && user.get().checkPassword(PasswordHasher.hashPassword(pass));
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
