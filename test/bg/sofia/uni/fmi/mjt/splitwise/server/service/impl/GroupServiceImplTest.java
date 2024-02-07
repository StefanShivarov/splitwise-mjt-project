package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.csv.GroupCsvProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.exception.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.Group;
import bg.sofia.uni.fmi.mjt.splitwise.server.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceImplTest {

    @Mock
    private GroupCsvProcessor groupCsvProcessorMock;

    @Mock
    private UserService userServiceMock;

    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        Set<Group> mockData = new HashSet<>();
        Set<User> firstGroupMembers = new HashSet<>();
        firstGroupMembers.add(new User("user1", "pass1"));
        firstGroupMembers.add(new User("user2", "pass2"));
        firstGroupMembers.add(new User("user3", "pass3"));
        Set<User> secondGroupMembers = new HashSet<>();
        secondGroupMembers.add(new User("user1", "pass1"));
        secondGroupMembers.add(new User("ivan", "12345"));
        secondGroupMembers.add(new User("georgi", "0000"));

        mockData.add(new Group("group1", firstGroupMembers));
        mockData.add(new Group("boys", secondGroupMembers));

        when(groupCsvProcessorMock.loadGroupsFromCsvFile())
                .thenReturn(mockData);

        groupService = new GroupServiceImpl(groupCsvProcessorMock, userServiceMock);
    }

    @Test
    void testGetGroupsForUserThrows() {
        when(userServiceMock.findUserByUsername("stefan"))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> groupService.getGroupsForUser("stefan"),
                "getGroupsForUser should throw UserNotFoundException "
                        + "if user does not exist!");
    }

    @Test
    void testGetGroupsForUser() throws UserNotFoundException {
        when(userServiceMock.findUserByUsername("user1"))
                .thenReturn(Optional.of(new User("user1", "pass1")));

        Collection<Group> groups = groupService.getGroupsForUser("user1");
        assertEquals(2, groups.size(),
                "getGroupsForUser should return correct groups!");

        Set<String> firstGroupMembers = groups.stream().toList()
                .get(0)
                .getMembers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());

        assertEquals(3, firstGroupMembers.size(),
                "getGroupsForUser should return correct groups!");
    }

    @Test
    void testFindGroupByName() {
        assertTrue(groupService.findGroupByName("boys").isPresent(),
                "findGroupByName should be present if group exists!");
        assertTrue(groupService.findGroupByName("girls").isEmpty(),
                "findGroupByName should be empty if group does not exist!");
    }

    @Test
    void testAddGroupThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> groupService.addGroup("", null),
                "addGroup should throw IllegalArgumentException if " +
                        "groupName or usernames are null or blank!");
    }

    @Test
    void testAddGroupAddsToCollection() {
        groupService.addGroup("team", Set.of("player1", "player2"));
        assertTrue(groupService.findGroupByName("team").isPresent(),
                "addGroup should add group to collection!");
    }

}
