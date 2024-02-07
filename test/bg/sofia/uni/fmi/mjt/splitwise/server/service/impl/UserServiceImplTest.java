package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.UserCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.PasswordHasher;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserCsvProcessor userCsvProcessorMock;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        Set<User> mockData = new HashSet<>();
        mockData.add(new User("ivan", PasswordHasher.hashPassword("0000"),
                "Ivan", "Ivanov"));
        mockData.add(new User("georgi", PasswordHasher.hashPassword("1234")));
        when(userCsvProcessorMock.loadUsersFromCsvFile())
                .thenReturn(mockData);

        userService = new UserServiceImpl(userCsvProcessorMock);
    }

    @Test
    void testFindUserByUsername() {
        Optional<User> ivan = userService.findUserByUsername("ivan");
        assertTrue(ivan.isPresent(),
                "optional should be present if user exists");
        assertEquals("ivan", ivan.get().getUsername(),
                "username should match the provided username");
        assertEquals(PasswordHasher.hashPassword("0000"), ivan.get().getHashedPassword(),
                "findUserByUsername should return correct user");
        assertEquals("Ivan Ivanov", ivan.get().getFullName(),
                "findUserByUsername should return correct user");
        assertEquals("Ivan Ivanov (ivan)", ivan.get().toString(),
                "findUserByUsername should return correct user");
        assertTrue(userService.findUserByUsername("georgi").isPresent(),
                "optional should be present if user exists");
        assertTrue(userService.findUserByUsername("petar").isEmpty(),
                "optional should be empty if user doesn't exist");
    }

    @Test
    void testAddUserAddsToCollection() {
        assertDoesNotThrow(() -> userService.addUser(
                "newUser", "pass", "", ""),
                "addUser should not throw if passed data is correct!");
        assertTrue(userService.findUserByUsername("newUser").isPresent(),
                "addUser should update the collection of users!");
    }

    @Test
    void testAddUserThrows() {
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(
                null, "", "name", "secondName"),
                "addUser should throw exception if username or"
                        + " pass are blank or null");
    }

}
