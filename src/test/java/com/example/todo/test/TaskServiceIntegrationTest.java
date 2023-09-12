package com.example.todo.test;

import com.example.todo.config.TestDbConfig;
import com.example.todo.config.TestIntegrationConfig;
import com.example.todo.persistence.dao.TaskRepository;
import com.example.todo.persistence.model.Task;
import com.example.todo.persistence.model.User;
import com.example.todo.service.TaskService;
import com.example.todo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Andrew
 * @since 11.09.2023
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class})
class TaskServiceIntegrationTest {
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository, userService);
    }

    @Test
    void testGetAllTasksByUserById() {
        Long userId = 1L;
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskRepository.findAllByUserId(userId)).thenReturn(tasks);

        List<Task> result = taskService.getAllTasksByUser(userId);

        assertEquals(tasks, result);
    }

    @Test
    void testGetAllTasksByUserByUsername() {
        String username = "user@example.com";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail(username);
        when(userService.findUserByEmail(username)).thenReturn(user);
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskRepository.findAllByUserId(userId)).thenReturn(tasks);

        List<Task> result = taskService.getAllTasksByUser(username);

        assertEquals(tasks, result);
    }

    @Test
    void testGetTaskById() {
        Long taskId = 1L;
        Task task = new Task();
        when(taskRepository.getReferenceById(taskId)).thenReturn(task);

        Task result = taskService.getTaskById(taskId);

        assertEquals(task, result);
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);

        assertNotNull(result);
    }

    @Test
    void testUpdateTask() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setDescription("Old Description");

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setDescription("New Description");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);

        Task result = taskService.updateTask(taskId, updatedTask);

        assertNotNull(result);
        assertEquals("New Description", result.getDescription());
    }

    @Test
    void testDeleteTask() {
        Long taskId = 1L;

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testCreateTaskWithDescriptionAndUser() {
        String description = "New Task";
        String username = "user@example.com";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail(username);
        when(userService.findUserByEmail(username)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        Task result = taskService.createTask(description, username);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testUpdateTaskStatus() {
        Long taskId = 1L;
        boolean done = true;
        Task task = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        boolean result = taskService.updateTaskStatus(taskId, done);

        assertTrue(result);
        assertTrue(task.isDone());
    }

    @Test
    void testUpdateTaskDescription() {
        Long taskId = 1L;
        String newDescription = "New Description";
        Task task = new Task();
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.updateTaskDescription(taskId, newDescription);

        assertNotNull(result);
        assertEquals(newDescription, result.getDescription());
    }
}