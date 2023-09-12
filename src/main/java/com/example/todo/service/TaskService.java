package com.example.todo.service;


import com.example.todo.persistence.dao.TaskRepository;
import com.example.todo.persistence.model.Task;
import com.example.todo.web.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrew
 * @since 06.09.2023
 */
@Service
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public List<Task> getAllTasksByUser(Long userId) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);
        Collections.reverse(tasks);
        return tasks;
    }

    @Override
    public List<Task> getAllTasksByUser(String username) {
        return getAllTasksByUser(userService.findUserByEmail(username).getId());
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.getReferenceById(id);
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task task) {
        Task taskFromDb = taskRepository.findById(id).orElse(null);
        if (taskFromDb != null) {
            taskFromDb.setDescription(task.getDescription());
            taskFromDb.setDone(task.isDone());
            taskFromDb.setUpdated(new Timestamp(System.currentTimeMillis()));
            taskRepository.save(taskFromDb);
            return taskFromDb;
        }
        return null;
    }

    @Override
    public Task updateTask(TaskDto task) {
        Task taskFromDb = taskRepository.findById(task.id()).orElse(null);
        if (taskFromDb != null) {
            taskFromDb.setDescription(task.description());
            taskFromDb.setDone(task.done());
            taskFromDb.setUpdated(new Timestamp(System.currentTimeMillis()));
            taskRepository.save(taskFromDb);
            return taskFromDb;
        }
        return null;
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task createTask(String description, String user) {
        Task task = new Task();
        task.setDescription(description);
        task.setUserId(userService.findUserByEmail(user).getId());
        task.setCreated(new Timestamp(System.currentTimeMillis()));
        System.out.println(task);
        return taskRepository.save(task);
    }

    @Override
    public boolean updateTaskStatus(Long id, boolean done) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setDone(done);
            task.setUpdated(new Timestamp(System.currentTimeMillis()));
            taskRepository.save(task);
            return true;
        }
        return false;
    }

    @Override
    public Task updateTaskDescription(Long id, String newDescription) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setDescription(newDescription);
            task.setUpdated(new Timestamp(System.currentTimeMillis()));
            System.out.println(task);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    public TaskDto getDto(Task task) {
        return new TaskDto(task.getId(), task.getDescription(), task.isDone());
    }
}
