package com.example.demo.service.impl;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.NotificationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> getAllNotifications(User user) {
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getPendingNotifications(User user) {
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getUser().getId().equals(user.getId()) && !notification.isRead())
                .collect(Collectors.toList());
    }

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
} 