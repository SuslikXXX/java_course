package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserService();
    }

    @Test
    void registerUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        assertNotNull(registeredUser.getId());
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("password123", registeredUser.getPassword());
        assertEquals("test@example.com", registeredUser.getEmail());
    }

    @Test
    void registerUser_DuplicateUsername() {
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPassword("password123");
        user1.setEmail("test1@example.com");

        User user2 = new User();
        user2.setUsername("testuser");
        user2.setPassword("password456");
        user2.setEmail("test2@example.com");

        userService.registerUser(user1);
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user2));
    }

    @Test
    void login_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        userService.registerUser(user);
        User loggedInUser = userService.login("testuser", "password123");

        assertNotNull(loggedInUser);
        assertEquals("testuser", loggedInUser.getUsername());
    }

    @Test
    void login_WrongPassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        userService.registerUser(user);
        User loggedInUser = userService.login("testuser", "wrongpassword");

        assertNull(loggedInUser);
    }

    @Test
    void getUserById_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        User registeredUser = userService.registerUser(user);
        User foundUser = userService.getUserById(registeredUser.getId());

        assertNotNull(foundUser);
        assertEquals(registeredUser.getId(), foundUser.getId());
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        User foundUser = userService.getUserById(999L);
        assertNull(foundUser);
    }

    @Test
    void getAllUsers_Success() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass2");
        user2.setEmail("user2@example.com");

        userService.registerUser(user1);
        userService.registerUser(user2);

        var users = userService.getAllUsers();
        assertEquals(2, users.size());
    }
} 