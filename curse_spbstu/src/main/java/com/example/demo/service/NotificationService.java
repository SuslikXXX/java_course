package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications(User user);
    List<Notification> getPendingNotifications(User user);
    Notification createNotification(Notification notification);
} 