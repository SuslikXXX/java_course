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
        // Given
        Notification notification = new Notification("Test message", testUser);

        // When
        Notification createdNotification = notificationService.createNotification(notification);

        // Then
        assertNotNull(createdNotification.getId());
        assertEquals("Test message", createdNotification.getMessage());
        assertEquals(testUser, createdNotification.getUser());
        assertNotNull(createdNotification.getCreationDate());
        assertFalse(createdNotification.isRead());
    }

    @Test
    void shouldReturnListOfAllNotifications() {
        // Given
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", anotherUser);
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);

        // When
        List<Notification> notifications = notificationService.getAllNotifications();

        // Then
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Message 1")));
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Message 2")));
    }

    @Test
    void shouldReturnOnlyNotificationsForSpecificUser() {
        // Given
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", anotherUser);
        Notification notification3 = new Notification("Message 3", testUser);
        notificationService.createNotification(notification1);
        notificationService.createNotification(notification2);
        notificationService.createNotification(notification3);

        // When
        List<Notification> userNotifications = notificationService.getAllNotifications(testUser);

        // Then
        assertEquals(2, userNotifications.size());
        assertTrue(userNotifications.stream().allMatch(n -> n.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void shouldReturnOnlyUnreadNotifications() {
        // Given
        Notification notification1 = new Notification("Message 1", testUser);
        Notification notification2 = new Notification("Message 2", testUser);
        notification1 = notificationService.createNotification(notification1);
        notification2 = notificationService.createNotification(notification2);
        
        notification2.setRead(true);
        notificationService.createNotification(notification2); // Update the notification

        // When
        List<Notification> pendingNotifications = notificationService.getPendingNotifications(testUser);

        // Then
        assertEquals(1, pendingNotifications.size());
        assertFalse(pendingNotifications.get(0).isRead());
        assertEquals("Message 1", pendingNotifications.get(0).getMessage());
    }

    @Test
    void shouldReturnNotificationWhenSearchingByValidId() {
        // Given
        Notification notification = new Notification("Test message", testUser);
        Notification createdNotification = notificationService.createNotification(notification);

        // When
        Notification foundNotification = notificationService.getNotificationById(createdNotification.getId());

        // Then
        assertNotNull(foundNotification);
        assertEquals(createdNotification.getId(), foundNotification.getId());
        assertEquals(createdNotification.getMessage(), foundNotification.getMessage());
    }

    @Test
    void shouldReturnNullWhenSearchingByInvalidId() {
        // When
        Notification foundNotification = notificationService.getNotificationById(999L);

        // Then
        assertNull(foundNotification);
    }
}
