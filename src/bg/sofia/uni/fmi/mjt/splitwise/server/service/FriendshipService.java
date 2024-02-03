package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;

import java.util.Collection;

public interface FriendshipService {

    Collection<User> getFriendsForUser(String username) throws UserNotFoundException;

    void addFriendship(String firstUsername, String secondUsername) throws UserNotFoundException;

}
