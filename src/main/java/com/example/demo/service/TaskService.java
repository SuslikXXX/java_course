package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();

    List<Task> getAllTasks(User user);

    List<Task> getPendingTasks(User user);

    Task createTask(Task task);

    void deleteTask(Long taskId);
    
    Task getTaskById(Long taskId);
} 