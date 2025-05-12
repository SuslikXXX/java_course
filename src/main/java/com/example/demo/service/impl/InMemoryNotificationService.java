package com.example.demo.service.impl;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryNotificationService implements NotificationService {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private long nextId = 1;

    @Override
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications.values());
    }

    @Override
    public List<Notification> getAllNotifications(User user) {
        return notifications.values().stream()
                .filter(notification -> notification.getUser() != null && 
                        notification.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getPendingNotifications(User user) {
        return notifications.values().stream()
                .filter(notification -> notification.getUser() != null && 
                        notification.getUser().getId().equals(user.getId()) && 
                        !notification.isRead())
                .collect(Collectors.toList());
    }

    @Override
    public Notification createNotification(Notification notification) {
        notification.setId(nextId++);
        notifications.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notifications.get(notificationId);
    }
} 