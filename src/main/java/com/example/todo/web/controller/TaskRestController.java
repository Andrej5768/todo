package com.example.todo.web.controller;

import com.example.todo.persistence.model.Task;
import com.example.todo.service.ITaskService;
import com.example.todo.web.dto.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Andrew
 * @since 06.09.2023
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {
    private static final Logger logger = LoggerFactory.getLogger(TaskRestController.class);

    private final ITaskService taskService;

    @Autowired
    public TaskRestController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all tasks",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        logger.info("Request to get all tasks");
        String username = getCurrentUsername();
        List<Task> tasks = taskService.getAllTasksByUser(username);
        logger.info("Response with all tasks for user {}", username);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by id")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task by id",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        logger.info("Request to get task by id {}", id);
        Task task = taskService.getTaskById(id);
        if (task != null) {
            logger.info("Response with task {}", task);
            return ResponseEntity.ok(taskService.getDto(task));
        } else {
            logger.warn("Task with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Successfully created task",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDescriptionDTO taskForm) {
        logger.info("Request to create task with description {}", taskForm.text);
        String username = getCurrentUsername();
        Task createdTask = taskService.createTask(taskForm.text, username);
        logger.info("Response with created task {}", createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.getDto(createdTask));
    }

    @Operation(summary = "Update task")
    @ApiResponse(responseCode = "200", description = "Successfully updated task",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto task) {
        logger.info("Request to update task {}", task);
        Task updatedTask = taskService.updateTask(task);
        if (updatedTask != null) {
            logger.info("Response with updated task {}", updatedTask);
            return ResponseEntity.ok(taskService.getDto(updatedTask));
        } else {
            logger.warn("Task with id {} not found", task.id());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete task")
    @ApiResponse(responseCode = "204", description = "Successfully deleted task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("Request to delete task with id {}", id);
        taskService.deleteTask(id);
        logger.info("Success deleted task with id {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update task description")
    @ApiResponse(responseCode = "200", description = "Successfully updated task description",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDto> updateTaskDescription(@PathVariable Long id, @RequestBody TaskDescriptionDTO taskForm) {
        logger.info("Request to update task description with id {}", id);
        TaskDto taskDto = taskService.getDto(taskService.updateTaskDescription(id, taskForm.text));
        if (taskDto != null) {
            logger.info("Response with updated task {}", taskDto);
            return ResponseEntity.ok(taskDto);
        } else {
            logger.warn("Task with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal()).getUsername();
    }

    public record TaskDescriptionDTO(String text) {
    }
}
