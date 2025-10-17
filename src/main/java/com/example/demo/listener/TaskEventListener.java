package com.example.demo.listener;

import com.example.demo.dto.TaskEventDTO;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class TaskEventListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public TaskEventListener(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "task-events", groupId = "notification-service-group")
    @Transactional
    public void handleTaskEvent(TaskEventDTO taskEvent) {
        log.info("Received task event from Kafka: {}", taskEvent);

        User user = userRepository.findById(taskEvent.getUserId()).orElse(null);
        if (user == null) {
            log.error("User not found with id: {}", taskEvent.getUserId());
            return;
        }

        Notification notification = new Notification("New task created: " + taskEvent.getTitle(), user);

        notificationService.createNotification(notification);
        
        log.info("Created notification for task: {}", taskEvent.getTitle());
    }
}
