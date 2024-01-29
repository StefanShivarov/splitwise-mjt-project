package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByUsername(String username);

    void addUser(String username, String pass, String firstName, String lastName);

}
