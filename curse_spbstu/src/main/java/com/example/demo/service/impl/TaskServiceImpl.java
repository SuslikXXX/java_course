package com.example.demo.service.impl;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks(User user) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(user.getId()) && !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasks(User user) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(user.getId()) && !task.isCompleted() && !task.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.findById(taskId).ifPresent(task -> {
            task.setDeleted(true);
            taskRepository.save(task);
        });
    }

    @Override
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .filter(task -> !task.isDeleted())
                .orElse(null);
    }
} 