package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncNotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Asynchronously creates a notification
     * @param message notification message
     * @param user user to notify
     * @return CompletableFuture with created notification
     */
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Notification> createNotificationAsync(String message, User user) {
        log.info("Async task started: Creating notification for user {} in thread {}", 
                user.getUsername(), Thread.currentThread().getName());
        
        try {
            // Simulate some processing time
            Thread.sleep(100);
            
            Notification notification = Notification.builder()
                    .message(message)
                    .user(user)
                    .build();
            
            notification = notificationRepository.save(notification);
            
            log.info("Async task completed: Notification created with ID {} for user {}", 
                    notification.getId(), user.getUsername());
            
            return CompletableFuture.completedFuture(notification);
        } catch (InterruptedException e) {
            log.error("Async task interrupted", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Asynchronously sends multiple notifications in batch
     * @param message notification message
     * @param users list of users to notify
     */
    @Async("taskExecutor")
    @Transactional
    public void sendBatchNotifications(String message, Iterable<User> users) {
        log.info("Async batch task started: Sending notifications in thread {}", 
                Thread.currentThread().getName());
        
        int count = 0;
        for (User user : users) {
            Notification notification = Notification.builder()
                    .message(message)
                    .user(user)
                    .build();
            
            notificationRepository.save(notification);
            count++;
        }
        
        log.info("Async batch task completed: Sent {} notifications", count);
    }

    /**
     * Asynchronously processes notification delivery (simulation)
     * @param notificationId ID of notification to process
     */
    @Async("taskExecutor")
    public CompletableFuture<Boolean> processNotificationDelivery(Long notificationId) {
        log.info("Async processing started: Processing notification {} in thread {}", 
                notificationId, Thread.currentThread().getName());
        
        try {
            // Simulate external service call or heavy processing
            Thread.sleep(500);
            
            log.info("Async processing completed: Notification {} delivered successfully", 
                    notificationId);
            
            return CompletableFuture.completedFuture(true);
        } catch (InterruptedException e) {
            log.error("Async processing interrupted for notification {}", notificationId, e);
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(false);
        }
    }
}
