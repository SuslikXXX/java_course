package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.InMemoryTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceCacheTest {

    @Autowired
    private InMemoryTaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void testGetTaskByIdCaching() {
        Task task = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .targetDate(LocalDateTime.now().plusDays(7))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();
        task = taskRepository.save(task);

        Task result1 = taskService.getTaskById(task.getId());
        assertNotNull(result1);
        assertEquals("Test Task", result1.getTitle());

        var cache = cacheManager.getCache("tasks");
        assertNotNull(cache);
        var cachedValue = cache.get(task.getId());
        assertNotNull(cachedValue);

        Task result2 = taskService.getTaskById(task.getId());
        assertNotNull(result2);
        assertEquals("Test Task", result2.getTitle());
        
        assertEquals(result1.getId(), result2.getId());
    }

    @Test
    void testGetAllTasksCaching() {
        Task task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .targetDate(LocalDateTime.now().plusDays(1))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();
        
        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .targetDate(LocalDateTime.now().plusDays(2))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> result1 = taskService.getAllTasks();
        assertNotNull(result1);
        assertEquals(2, result1.size());

        var cache = cacheManager.getCache("allTasks");
        assertNotNull(cache);

        List<Task> result2 = taskService.getAllTasks();
        assertNotNull(result2);
        assertEquals(2, result2.size());
    }

    @Test
    void testCacheEvictionOnCreate() {
        List<Task> initialTasks = taskService.getAllTasks();
        assertTrue(initialTasks.isEmpty());

        Task newTask = Task.builder()
                .title("New Task")
                .description("New Description")
                .targetDate(LocalDateTime.now().plusDays(5))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();
        
        taskService.createTask(newTask);

        List<Task> tasksAfterCreate = taskService.getAllTasks();
        assertEquals(1, tasksAfterCreate.size());
        assertEquals("New Task", tasksAfterCreate.get(0).getTitle());
    }

    @Test
    void testCacheEvictionOnDelete() {
        Task task = Task.builder()
                .title("Task to Delete")
                .description("Will be deleted")
                .targetDate(LocalDateTime.now().plusDays(3))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();
        task = taskRepository.save(task);

        Task cachedTask = taskService.getTaskById(task.getId());
        assertNotNull(cachedTask);

        taskService.deleteTask(task.getId());

        var cache = cacheManager.getCache("tasks");
        assertNotNull(cache);
        var cachedValue = cache.get(task.getId());
        assertNull(cachedValue);
    }

    @Test
    void testUserTasksCaching() {
        Task task = Task.builder()
                .title("User Task")
                .description("Task for user")
                .targetDate(LocalDateTime.now().plusDays(4))
                .user(testUser)
                .completed(false)
                .deleted(false)
                .build();
        taskRepository.save(task);

        List<Task> userTasks1 = taskService.getAllTasks(testUser);
        assertNotNull(userTasks1);
        assertEquals(1, userTasks1.size());

        var cache = cacheManager.getCache("userTasks");
        assertNotNull(cache);
        var cachedValue = cache.get(testUser.getId());
        assertNotNull(cachedValue);

        List<Task> userTasks2 = taskService.getAllTasks(testUser);
        assertNotNull(userTasks2);
        assertEquals(1, userTasks2.size());
    }
}
