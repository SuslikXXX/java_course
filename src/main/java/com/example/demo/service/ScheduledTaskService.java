package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.Task;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Checks for overdue tasks every 5 minutes and creates notifications
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes (300000 ms)
    @Transactional
    public void checkOverdueTasks() {
        log.info("Scheduled task: Checking for overdue tasks...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = taskRepository.findByTargetDateBeforeAndCompletedFalseAndDeletedFalse(now);
        
        log.info("Found {} overdue tasks", overdueTasks.size());
        
        for (Task task : overdueTasks) {
            // Check if notification already exists for this overdue task
            boolean notificationExists = notificationRepository.findByUser(task.getUser())
                    .stream()
                    .anyMatch(n -> n.getMessage().contains("Task overdue: " + task.getTitle()));
            
            if (!notificationExists) {
                Notification notification = Notification.builder()
                        .message("Task overdue: " + task.getTitle() + 
                                ". Target date was: " + task.getTargetDate())
                        .user(task.getUser())
                        .build();
                
                notificationRepository.save(notification);
                log.info("Created overdue notification for task: {} (User: {})", 
                        task.getTitle(), task.getUser().getUsername());
            }
        }
        
        log.info("Scheduled task: Overdue check completed");
    }

    /**
     * Alternative scheduled method using cron expression
     * Runs every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void dailyOverdueTasksSummary() {
        log.info("Daily scheduled task: Checking overdue tasks summary...");
        
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = taskRepository.findByTargetDateBeforeAndCompletedFalseAndDeletedFalse(now);
        
        if (!overdueTasks.isEmpty()) {
            log.warn("Daily Summary: {} overdue tasks found", overdueTasks.size());
            overdueTasks.forEach(task -> 
                log.warn("Overdue task: {} (User: {}, Target date: {})", 
                        task.getTitle(), task.getUser().getUsername(), task.getTargetDate())
            );
        } else {
            log.info("Daily Summary: No overdue tasks");
        }
    }
}
