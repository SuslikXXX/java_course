package com.example.demo.service.impl;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryNotificationService implements NotificationService {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Notification> getAllNotifications(User user) {
        return notifications.values().stream()
                .filter(notification -> notification.getUser().equals(user))
                .toList();
    }

    @Override
    public List<Notification> getPendingNotifications(User user) {
        return notifications.values().stream()
                .filter(notification -> notification.getUser().equals(user) && !notification.isRead())
                .toList();
    }

    @Override
    public Notification createNotification(Notification notification) {
        notification.setId(idCounter.getAndIncrement());
        notifications.put(notification.getId(), notification);
        return notification;
    }
} 