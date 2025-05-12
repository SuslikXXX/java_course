package com.example.demo.service.impl;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryTaskService implements TaskService {
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Task> getAllTasks(User user) {
        return tasks.values().stream()
                .filter(task -> task.getUser().equals(user) && !task.isDeleted())
                .toList();
    }

    @Override
    public List<Task> getPendingTasks(User user) {
        return tasks.values().stream()
                .filter(task -> task.getUser().equals(user) && !task.isCompleted() && !task.isDeleted())
                .toList();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(idCounter.getAndIncrement());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteTask(Long taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setDeleted(true);
        }
    }

    @Override
    public Task getTaskById(Long taskId) {
        Task task = tasks.get(taskId);
        return (task != null && !task.isDeleted()) ? task : null;
    }
} 