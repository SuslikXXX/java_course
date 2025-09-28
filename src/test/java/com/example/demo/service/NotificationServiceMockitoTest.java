package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.service.impl.InMemoryNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceMockitoTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private InMemoryNotificationService notificationService;

    @Test
    @DisplayName("createNotification: saves and returns notification")
    void createNotification_success() {
        User user = User.builder().id(1L).username("u").email("u@e.com").password("p").build();
        Notification input = Notification.builder()
                .message("Hello")
                .user(user)
                .build();

        Notification saved = Notification.builder()
                .id(10L)
                .message("Hello")
                .user(user)
                .read(false)
                .creationDate(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        Notification result = notificationService.createNotification(input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.isRead()).isFalse();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).isEqualTo("Hello");
    }

    @Test
    @DisplayName("getAllNotifications: returns all")
    void getAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(List.of(new Notification(), new Notification()));
        assertThat(notificationService.getAllNotifications()).hasSize(2);
    }

    @Test
    @DisplayName("getAllNotifications(user): returns specific user notifications")
    void getAllNotificationsByUser() {
        User user = User.builder().id(2L).build();
        when(notificationRepository.findByUser(user)).thenReturn(List.of(new Notification()));
        assertThat(notificationService.getAllNotifications(user)).hasSize(1);
    }

    @Test
    @DisplayName("getPendingNotifications: returns unread only")
    void getPendingNotifications() {
        User user = User.builder().id(3L).build();
        Notification n1 = Notification.builder().id(1L).read(false).build();
        Notification n2 = Notification.builder().id(2L).read(false).build();
        when(notificationRepository.findByUserAndReadFalse(user)).thenReturn(List.of(n1, n2));
        assertThat(notificationService.getPendingNotifications(user)).hasSize(2);
    }

    @Test
    @DisplayName("getNotificationById: found returns notification")
    void getNotificationById_found() {
        Notification n = Notification.builder().id(5L).message("M").build();
        when(notificationRepository.findById(5L)).thenReturn(Optional.of(n));
        assertThat(notificationService.getNotificationById(5L)).isNotNull();
    }

    @Test
    @DisplayName("getNotificationById: not found returns null")
    void getNotificationById_notFound() {
        when(notificationRepository.findById(42L)).thenReturn(Optional.empty());
        assertThat(notificationService.getNotificationById(42L)).isNull();
    }
}
