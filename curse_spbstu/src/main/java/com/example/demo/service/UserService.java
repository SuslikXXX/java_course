package com.example.demo.service;

import com.example.demo.model.User;

public interface UserService {
    User registerUser(User user);
    User login(String username, String password);
    User getUserById(Long userId);
} 