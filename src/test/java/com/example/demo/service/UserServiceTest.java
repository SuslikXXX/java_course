package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserService();
    }

    @Test
    void shouldSuccessfullyRegisterNewUser() {
        User user = new User("testUser", "test@example.com");
        user.setPassword("password123");

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser.getId());
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("password123", registeredUser.getPassword());
    }

    @Test
    void shouldNotAllowDuplicateUsernames() {
        User user1 = new User("testUser", "test1@example.com");
        User user2 = new User("testUser", "test2@example.com");

        userService.registerUser(user1);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user2));
    }

    @Test
    void shouldSuccessfullyLoginWithValidCredentials() {
        User user = new User("testUser", "test@example.com");
        user.setPassword("password123");
        userService.registerUser(user);

        User loggedInUser = userService.login("testUser", "password123");

        assertNotNull(loggedInUser);
        assertEquals("testUser", loggedInUser.getUsername());
    }

    @Test
    void shouldRejectLoginWithInvalidCredentials() {
        User user = new User("testUser", "test@example.com");
        user.setPassword("password123");
        userService.registerUser(user);

        User loggedInUser = userService.login("testUser", "wrongPassword");

        assertNull(loggedInUser);
    }

    @Test
    void shouldReturnListOfAllRegisteredUsers() {
        User user1 = new User("user1", "test1@example.com");
        User user2 = new User("user2", "test2@example.com");
        userService.registerUser(user1);
        userService.registerUser(user2);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    void shouldReturnUserWhenSearchingByValidId() {
        User user = new User("testUser", "test@example.com");
        User registeredUser = userService.registerUser(user);

        User foundUser = userService.getUserById(registeredUser.getId());

        assertNotNull(foundUser);
        assertEquals(registeredUser.getId(), foundUser.getId());
        assertEquals(registeredUser.getUsername(), foundUser.getUsername());
    }

    @Test
    void shouldReturnNullWhenSearchingByInvalidId() {
        User foundUser = userService.getUserById(999L);

        assertNull(foundUser);
    }
}
