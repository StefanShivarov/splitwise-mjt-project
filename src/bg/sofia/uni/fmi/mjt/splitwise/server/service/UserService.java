package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserById(long id);

}
