package bg.sofia.uni.fmi.mjt.splitwise.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserById(long id);

}
