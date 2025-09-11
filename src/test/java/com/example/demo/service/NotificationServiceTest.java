package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;
    private User testUser;

    @BeforeEach
    void setUp() {
        notificationService = new InMemoryNotificationService();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
    }

    @Test
    void createNotification_Success() {
        Notification notification = new Notification();
        notification.setMessage("Test notification");
        notification.setUser(testUser);

        Notification savedNotification = notificationService.createNotification(notification);

        assertNotNull(savedNotification);
        assertNotNull(savedNotification.getId());
        assertEquals("Test notification", savedNotification.getMessage());
        assertEquals(testUser.getId(), savedNotification.getUser().getId());
    }

    @Test
    void getAllNotifications_Success() {
        // Create and save test notifications
        Notification notification1 = new Notification();
        notification1.setMessage("Test 1");
        notification1.setUser(testUser);

        Notification notification2 = new Notification();
        notification2.setMessage("Test 2");
        notification2.setUser(testUser);

        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        List<Notification> userNotifications = notificationService.getAllNotifications(testUser);

        assertNotNull(userNotifications);
        assertEquals(2, userNotifications.size());
        assertTrue(userNotifications.stream()
                .allMatch(n -> n.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void getPendingNotifications_Success() {
        // Create and save test notifications
        Notification notification1 = new Notification();
        notification1.setMessage("Pending 1");
        notification1.setUser(testUser);

        Notification notification2 = new Notification();
        notification2.setMessage("Pending 2");
        notification2.setUser(testUser);
        notification2.setRead(true);

        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        List<Notification> pendingNotifications = notificationService.getPendingNotifications(testUser);

        assertNotNull(pendingNotifications);
        assertEquals(1, pendingNotifications.size());
        assertFalse(pendingNotifications.get(0).isRead());
    }

    @Test
    void getNotificationById_Success() {
        Notification notification = new Notification();
        notification.setMessage("Test notification");
        notification.setUser(testUser);

        Notification savedNotification = notificationService.createNotification(notification);
        Notification retrievedNotification = notificationService.getNotificationById(savedNotification.getId());

        assertNotNull(retrievedNotification);
        assertEquals(savedNotification.getId(), retrievedNotification.getId());
        assertEquals(savedNotification.getMessage(), retrievedNotification.getMessage());
    }

    @Test
    void getNotificationById_NotFound() {
        Notification notification = notificationService.getNotificationById(999L);
        assertNull(notification);
    }
}
