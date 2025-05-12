package com.example.demo.service.impl;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryTaskService implements TaskService {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private long nextId = 1;

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getAllTasks(User user) {
        return tasks.values().stream()
                .filter(task -> task.getUser() != null && task.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasks(User user) {
        return tasks.values().stream()
                .filter(task -> task.getUser() != null && 
                        task.getUser().getId().equals(user.getId()) && 
                        !task.isCompleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task createTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteTask(Long taskId) {
        tasks.remove(taskId);
    }

    @Override
    public Task getTaskById(Long taskId) {
        return tasks.get(taskId);
    }
} 