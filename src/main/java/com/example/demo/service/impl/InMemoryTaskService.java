package com.example.demo.service.impl;

import com.example.demo.dto.TaskEventDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class InMemoryTaskService implements TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private KafkaTemplate<String, TaskEventDTO> kafkaTemplate;

    @Override
    @Cacheable(value = "allTasks", unless = "#result == null || #result.isEmpty()")
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    @Cacheable(value = "userTasks", key = "#user.id", unless = "#result == null || #result.isEmpty()")
    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUserAndDeletedFalse(user);
    }

    @Override
    @Cacheable(value = "pendingTasks", key = "#user.id", unless = "#result == null || #result.isEmpty()")
    public List<Task> getPendingTasks(User user) {
        return taskRepository.findByUserAndDeletedFalse(user).stream()
                .filter(task -> !task.isCompleted())
                .toList();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "allTasks", allEntries = true),
        @CacheEvict(value = "userTasks", key = "#task.user.id"),
        @CacheEvict(value = "pendingTasks", key = "#task.user.id")
    })
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        
        try {
            TaskEventDTO taskEvent = TaskEventDTO.builder()
                    .taskId(savedTask.getId())
                    .title(savedTask.getTitle())
                    .description(savedTask.getDescription())
                    .userId(savedTask.getUser().getId())
                    .username(savedTask.getUser().getUsername())
                    .createdAt(savedTask.getCreationDate())
                    .eventType("TASK_CREATED")
                    .build();
            
            kafkaTemplate.send("task-events", taskEvent);
            log.info("Sent task event to Kafka: {}", taskEvent);
        } catch (Exception e) {
            log.error("Failed to send task event to Kafka", e);
        }
        
        return savedTask;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "tasks", key = "#taskId"),
        @CacheEvict(value = "allTasks", allEntries = true),
        @CacheEvict(value = "userTasks", allEntries = true),
        @CacheEvict(value = "pendingTasks", allEntries = true)
    })
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    @Cacheable(value = "tasks", key = "#taskId", unless = "#result == null")
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }
} 