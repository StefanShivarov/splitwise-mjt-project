package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.FriendshipCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Friendship;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceImplTest {

    @Mock
    private FriendshipCsvProcessor friendshipCsvProcessorMock;

    @Mock
    private UserService userServiceMock;

    private FriendshipServiceImpl friendshipService;

    @BeforeEach
    void setUp() {
        Set<Friendship> mockData = new HashSet<>();
        mockData.add(new Friendship(
                new User("ivan", "12345"),
                new User("georgi", "0000")
        ));

        mockData.add(new Friendship(
                new User("ivan", "12345"),
                new User("petar", "7777")
        ));

        when(friendshipCsvProcessorMock.loadFriendshipsFromCsvFile())
                .thenReturn(mockData);

        friendshipService = new FriendshipServiceImpl(
                friendshipCsvProcessorMock, userServiceMock);
    }

    @Test
    void testGetFriendsForUserThrows() {
        when(userServiceMock.findUserByUsername("joro"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> friendshipService.getFriendsForUser("joro"),
                "getFriendsForUser should throw UserNotFoundException if "
                        + "user does not exist!");
    }

    @Test
    void testGetFriendsForUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        assertDoesNotThrow(() -> friendshipService.getFriendsForUser("ivan"),
                "getFriendsForUser should not throw if user is found");
        assertEquals(2, friendshipService.getFriendsForUser("ivan").size(),
                "getFriendsForUser should return correct friendships for given user");
    }

    @Test
    void testCheckFriendship() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        assertTrue(friendshipService.checkFriendship("ivan", "georgi"));
        assertFalse(friendshipService.checkFriendship("ivan", "kaloyan"));
    }

    @Test
    void testAddFriendshipAddsToCollection() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));
        when(userServiceMock.findUserByUsername("pavel"))
                .thenReturn(Optional.of(new User("pavel", "4444")));

        assertFalse(friendshipService.checkFriendship("ivan", "pavel"),
                "friendship should not exist before it is added");
        assertDoesNotThrow(() -> friendshipService.addFriendship("ivan", "pavel"),
                "addFriendship should not throw exception if both users exist");
        assertTrue(friendshipService.checkFriendship("ivan", "pavel"),
                "friendship should exist after it is added");
    }

    @Test
    void testAddFriendshipThrows() {
        when(userServiceMock.findUserByUsername("kalin"))
                .thenReturn(Optional.empty());
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan", "12345")));

        assertThrows(IllegalArgumentException.class,
                () -> friendshipService.addFriendship(null, "joro"),
                "should throw IllegalArgumentException " +
                        "if one of the usernames is null or blank!");

        assertThrows(UserNotFoundException.class,
                () -> friendshipService.addFriendship("ivan", "kalin"),
                "should throw UserNotFoundException if one " +
                        "of the users doesn't exist!");

        assertThrows(UserNotFoundException.class,
                () -> friendshipService.addFriendship("kalin", "ivan"),
                "should throw UserNotFoundException if one " +
                        "of the users doesn't exist!");
    }

}
