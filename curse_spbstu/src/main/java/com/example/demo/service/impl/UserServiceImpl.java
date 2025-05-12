package com.example.demo.service.impl;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        logger.info("Attempting to register user: {}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Username already exists: {}", user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    public User login(String username, String password) {
        logger.info("Attempting to login user: {}", username);
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            logger.info("User logged in successfully: {}", username);
            return user;
        }
        logger.warn("Login failed for user: {}", username);
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId).orElse(null);
    }
} 