package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;


import bg.sofia.uni.fmi.mjt.splitwise.server.csv.UserCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.PasswordHasher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {

    @Mock
    private static UserCsvProcessor userCsvProcessor;

    @InjectMocks
    private static UserServiceImpl userService;

    @BeforeAll
    static void setUp() {
        when(userCsvProcessor.loadUsersFromCsvFile())
                .thenReturn(Set.of(
                        new User("ivan", PasswordHasher.hashPassword("0000"),
                                "Ivan", "Ivanov"),
                        new User("georgi", PasswordHasher.hashPassword("1234"))));
    }

    @Test
    void testFindUserByUsername() {
        Optional<User> ivan = userService.findUserByUsername("ivan");
        assertTrue(ivan.isPresent(),
                "optional should be present if user exists");
        assertEquals("ivan", ivan.get().getUsername(),
                "username should match the provided username");
        assertEquals(PasswordHasher.hashPassword("0000"), ivan.get().getHashedPassword());
        assertEquals("Ivan Ivanov", ivan.get().getFullName());
        assertEquals("Ivan Ivanov (ivan)", ivan.toString());
        assertTrue(userService.findUserByUsername("georgi").isPresent());
        assertTrue(userService.findUserByUsername("petar").isEmpty());
    }

}
