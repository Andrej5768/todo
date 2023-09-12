package com.example.todo.service;


import com.example.todo.persistence.model.Task;
import com.example.todo.web.dto.TaskDto;

import java.util.List;

/**
 * @author Andrew
 * @since 06.09.2023
 */
public interface ITaskService {
    List<Task> getAllTasksByUser(Long userId);

    List<Task> getAllTasksByUser(String username);

    Task getTaskById(Long id);

    Task createTask(Task task);

    Task updateTask(Long id, Task task);

    Task updateTask(TaskDto task);

    void deleteTask(Long id);

    Task createTask(String description, String user);

    boolean updateTaskStatus(Long id, boolean done);

    Task updateTaskDescription(Long id, String newDescription);

    TaskDto getDto(Task task);
}
