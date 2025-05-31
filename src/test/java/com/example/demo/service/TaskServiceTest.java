package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryTaskService;
import com.example.demo.service.impl.InMemoryUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    private TaskService taskService;
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        taskService = new InMemoryTaskService();
        userService = new InMemoryUserService();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser = userService.registerUser(testUser);
    }

    @Test
    void createTask_Success() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(testUser);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals(testUser.getId(), createdTask.getUser().getId());
        assertFalse(createdTask.isCompleted());
    }

    @Test
    void getAllTasks_Success() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUser(testUser);

        taskService.createTask(task1);
        taskService.createTask(task2);

        var tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void getAllTasksByUser_Success() {
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setPassword("pass123");
        anotherUser.setEmail("another@example.com");
        anotherUser = userService.registerUser(anotherUser);

        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUser(anotherUser);

        taskService.createTask(task1);
        taskService.createTask(task2);

        var userTasks = taskService.getAllTasks(testUser);
        assertEquals(1, userTasks.size());
        assertEquals("Task 1", userTasks.get(0).getTitle());
    }

    @Test
    void getPendingTasks_Success() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUser(testUser);
        task2.setCompleted(true);

        taskService.createTask(task1);
        taskService.createTask(task2);

        var pendingTasks = taskService.getPendingTasks(testUser);
        assertEquals(1, pendingTasks.size());
        assertEquals("Task 1", pendingTasks.get(0).getTitle());
    }

    @Test
    void getTaskById_Success() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(testUser);

        Task createdTask = taskService.createTask(task);
        Task foundTask = taskService.getTaskById(createdTask.getId());

        assertNotNull(foundTask);
        assertEquals(createdTask.getId(), foundTask.getId());
        assertEquals("Test Task", foundTask.getTitle());
    }

    @Test
    void getTaskById_NotFound() {
        Task foundTask = taskService.getTaskById(999L);
        assertNull(foundTask);
    }

    @Test
    void deleteTask_Success() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUser(testUser);

        Task createdTask = taskService.createTask(task);
        taskService.deleteTask(createdTask.getId());

        Task deletedTask = taskService.getTaskById(createdTask.getId());
        assertNull(deletedTask);
    }
} 