package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private NotificationService notificationService;
    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        notificationService = new InMemoryNotificationService();
        testUser = new User("testUser", "test@example.com");
        testUser.setId(1L);
        anotherUser = new User("anotherUser", "another@example.com");
        anotherUser.setId(2L);
    }

    @Test
    void shouldSuccessfullyCreateNewNotification() {
        Notification notification = new Notification("Test message", testUser);

        Notification createdNotification = notificationService.createNotification(notification);

        assertNotNull(createdNotification.getId());
        assertEquals("Test message", createdNotification.getMessage());
        assertEquals(testUser, createdNotification.getUser());
        assertNotNull(createdNotification.getCreationDate());
        assertFalse(createdNotification.isRead());
    }

    @Test
    void shouldReturnListOfAllNotifications() {
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", anotherUser);
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        List<Notification> notifications = notificationService.getAllNotifications();

        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Message 1")));
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Message 2")));
    }

    @Test
    void shouldReturnOnlyNotificationsForSpecificUser() {
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", anotherUser);
        Notification notification3 = new Notification("Message 3", testUser);
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);
        notificationService.createNotification(notification3);

        List<Notification> userNotifications = notificationService.getAllNotifications(testUser);

        assertEquals(2, userNotifications.size());
        assertTrue(userNotifications.stream().allMatch(n -> n.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void shouldReturnOnlyUnreadNotifications() {
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", testUser);
        notification1 = notificationService.createNotification(notification1);
        notification2 = notificationService.createNotification(notification2);
        
        notification2.setRead(true);
        notificationService.createNotification(notification2); // Update the notification

        List<Notification> pendingNotifications = notificationService.getPendingNotifications(testUser);

        assertEquals(1, pendingNotifications.size());
        assertFalse(pendingNotifications.get(0).isRead());
        assertEquals("Message 1", pendingNotifications.get(0).getMessage());
    }

    @Test
    void shouldReturnNotificationWhenSearchingByValidId() {
        Notification notification = new Notification("Test message", testUser);
        Notification createdNotification = notificationService.createNotification(notification);

        Notification foundNotification = notificationService.getNotificationById(createdNotification.getId());

        assertNotNull(foundNotification);
        assertEquals(createdNotification.getId(), foundNotification.getId());
        assertEquals(createdNotification.getMessage(), foundNotification.getMessage());
    }

    @Test
    void shouldReturnNullWhenSearchingByInvalidId() {
        Notification foundNotification = notificationService.getNotificationById(999L);

        assertNull(foundNotification);
    }
}
