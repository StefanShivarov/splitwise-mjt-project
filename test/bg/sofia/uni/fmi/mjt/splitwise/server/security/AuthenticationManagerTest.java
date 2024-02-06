package bg.sofia.uni.fmi.mjt.splitwise.server.security;

import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationManagerTest {

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private AuthenticationManager authenticationManager;

    @Test
    void testUnauthenticatedAtStart() {
        assertNull(authenticationManager.getAuthenticatedUser(),
                "authenticated user should be null before authentication!");
        assertFalse(authenticationManager.isAuthenticated(),
                "isAuthenticated should return false if user is not authenticated");
    }

    @Test
    void testAuthenticate() {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan",
                        PasswordHasher.hashPassword("0000"))));

        when(userServiceMock.findUserByUsername("georgi"))
                .thenReturn(Optional.empty());

        assertTrue(authenticationManager.authenticate("ivan", "0000"),
                "authenticate should return true if user exists!");
        assertNotNull(authenticationManager.getAuthenticatedUser(),
                "authenticated user shouldn't be null!");
        assertTrue(authenticationManager.isAuthenticated(),
                "isAuthenticated should return true if user is authenticated!");
        assertFalse(authenticationManager.authenticate("georgi", "pass"),
                "authenticate should return false if user doesn't exist!");
        assertFalse(authenticationManager.authenticate("ivan", "1111"),
                "authenticate should return false if password doesn't match!");
    }

    @Test
    void testLogout() {
        when(userServiceMock.findUserByUsername("ivan"))
                .thenReturn(Optional.of(new User("ivan",
                        PasswordHasher.hashPassword("0000"))));
        assertTrue(authenticationManager.authenticate("ivan", "0000"),
                "authenticate should return true if user exists!");
        assertNotNull(authenticationManager.getAuthenticatedUser(),
                "authenticated user shouldn't be null!");
        authenticationManager.logout();
        assertNull(authenticationManager.getAuthenticatedUser(),
                "authenticated user should be null after logout!");
        assertFalse(authenticationManager.isAuthenticated(),
                "is authenticated should return false after logout!");
    }

}
