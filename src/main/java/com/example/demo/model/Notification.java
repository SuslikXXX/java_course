package com.example.demo.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String message;
    private LocalDateTime creationDate;
    private boolean read;
    private User user;

    public Notification() {
        this.creationDate = LocalDateTime.now();
        this.read = false;
    }

    public Notification(String message, User user) {
        this();
        this.message = message;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 