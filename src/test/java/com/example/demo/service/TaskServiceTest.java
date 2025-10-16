package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.service.impl.InMemoryTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;
    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        taskService = new InMemoryTaskService();
        testUser = new User("testUser", "test@example.com");
        testUser.setId(1L);
        anotherUser = new User("anotherUser", "another@example.com");
        anotherUser.setId(2L);
    }

    @Test
    void shouldSuccessfullyCreateNewTask() {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(1);
        Task task = new Task("Test Task", "Test Description", targetDate, testUser);

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask.getId());
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals(targetDate, createdTask.getTargetDate());
        assertEquals(testUser, createdTask.getUser());
        assertFalse(createdTask.isCompleted());
        assertFalse(createdTask.isDeleted());
    }

    @Test
    void shouldReturnListOfAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now(), testUser);
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now(), anotherUser);
        taskService.createTask(task1);
        taskService.createTask(task2);

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Task 2")));
    }

    @Test
    void shouldReturnOnlyTasksForSpecificUser() {
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now(), testUser);
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now(), anotherUser);
        Task task3 = new Task("Task 3", "Description 3", LocalDateTime.now(), testUser);
        taskService.createTask(task1);
        taskService.createTask(task2);
        taskService.createTask(task3);

        List<Task> userTasks = taskService.getAllTasks(testUser);

        assertEquals(2, userTasks.size());
        assertTrue(userTasks.stream().allMatch(t -> t.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void shouldReturnOnlyPendingTasksForUser() {
        Task task1 = new Task("Task 1", "Description 1", LocalDateTime.now(), testUser);
        Task task2 = new Task("Task 2", "Description 2", LocalDateTime.now(), testUser);
        task1 = taskService.createTask(task1);
        task2 = taskService.createTask(task2);
        
        task2.setCompleted(true);
        taskService.createTask(task2); // Update the task

        List<Task> pendingTasks = taskService.getPendingTasks(testUser);

        assertEquals(1, pendingTasks.size());
        assertFalse(pendingTasks.get(0).isCompleted());
        assertEquals("Task 1", pendingTasks.get(0).getTitle());
    }

    @Test
    void shouldReturnTaskWhenSearchingByValidId() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), testUser);
        Task createdTask = taskService.createTask(task);

        Task foundTask = taskService.getTaskById(createdTask.getId());

        assertNotNull(foundTask);
        assertEquals(createdTask.getId(), foundTask.getId());
        assertEquals(createdTask.getTitle(), foundTask.getTitle());
    }

    @Test
    void shouldReturnNullWhenSearchingByInvalidId() {
        Task foundTask = taskService.getTaskById(999L);

        assertNull(foundTask);
    }

    @Test
    void shouldSuccessfullyDeleteTask() {
        Task task = new Task("Test Task", "Description", LocalDateTime.now(), testUser);
        Task createdTask = taskService.createTask(task);

        taskService.deleteTask(createdTask.getId());
        Task deletedTask = taskService.getTaskById(createdTask.getId());

        assertNull(deletedTask);
    }
}
