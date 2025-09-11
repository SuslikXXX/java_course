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
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user2);
        });
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
    void login_UserNotFound() {
        User loggedInUser = userService.login("nonexistent", "password123");
        assertNull(loggedInUser);
    }

    @Test
    void getUserById_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        User registeredUser = userService.registerUser(user);
        User retrievedUser = userService.getUserById(registeredUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(registeredUser.getId(), retrievedUser.getId());
        assertEquals("testuser", retrievedUser.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        User user = userService.getUserById(999L);
        assertNull(user);
    }
}
