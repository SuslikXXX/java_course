package com.example.demo.service.impl;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryTaskService implements TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

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
        return taskRepository.save(task);
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