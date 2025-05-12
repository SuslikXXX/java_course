package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryUserService implements UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User registerUser(User user) {
        if (usersByUsername.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setId(idCounter.getAndIncrement());
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }
} 