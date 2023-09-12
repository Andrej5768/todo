package com.example.todo.web.controller;

import com.example.todo.persistence.model.Task;
import com.example.todo.service.ITaskService;
import com.example.todo.web.dto.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
        String username = getCurrentUsername();
        List<Task> tasks = taskService.getAllTasksByUser(username);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by id")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task by id",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(taskService.getDto(task));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Successfully created task",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)))
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDescriptionDTO taskForm) {
        String username = getCurrentUsername();
        Task createdTask = taskService.createTask(taskForm.text, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "Update task")
    @ApiResponse(responseCode = "200", description = "Successfully updated task",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskDto task) {
        Task updatedTask = taskService.updateTask(task);
        if (updatedTask != null) {
            return ResponseEntity.ok(taskService.getDto(updatedTask));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete task")
    @ApiResponse(responseCode = "204", description = "Successfully deleted task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update task description")
    @ApiResponse(responseCode = "200", description = "Successfully updated task description",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Task.class)))
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping("/update/{id}")
    public ResponseEntity<TaskDto> updateTaskDescription(@PathVariable Long id, @RequestBody TaskDescriptionDTO taskForm) {
        TaskDto taskDto = taskService.getDto(taskService.updateTaskDescription(id, taskForm.text));
        if (taskDto != null) {
            return ResponseEntity.ok(taskDto);
        } else {
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
