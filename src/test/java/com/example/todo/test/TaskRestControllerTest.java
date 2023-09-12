package com.example.todo.test;

import com.example.todo.config.TestDbConfig;
import com.example.todo.config.TestIntegrationConfig;
import com.example.todo.persistence.dao.TaskRepository;
import com.example.todo.persistence.dao.UserRepository;
import com.example.todo.persistence.model.Task;
import com.example.todo.persistence.model.User;
import com.example.todo.security.JwtProvider;
import com.example.todo.service.IUserService;
import com.example.todo.service.TaskService;
import com.example.todo.web.dto.TaskDto;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@SpringJUnitConfig
@SpringBootTest(classes = { TestDbConfig.class, TestIntegrationConfig.class})
@AutoConfigureMockMvc
public class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    int port = 8081;

    private String TOKEN;

    @Autowired
    private JwtProvider jwtProvider;
    @Mock
    private IUserService userService;

    private User user;
    @Autowired
    private TaskRepository taskRepository;


    //

    @BeforeEach
    public void init() {
        user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setId(1L);
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            user = userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            user = userRepository.save(user);
        }

        TOKEN = jwtProvider.generateToken(user.getEmail(), user.getRoles());
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetAllTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        tasks.add(taskService.createTask("new task 1", "test@test.com"));
        tasks.add(taskService.createTask("new task 2", "test@test.com"));
        tasks.add(taskService.createTask("new task 3", "test@test.com"));

        Mockito.when(userService.findUserByEmail(anyString())).thenReturn(user);
        Mockito.when(taskService.getAllTasksByUser(anyString())).thenReturn(tasks);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetTaskById() throws Exception {
        Long taskId = 1L;
        Task task = new Task();
        task.setDescription("Task description");
        task.setUserId(user.getId());
        task.setId(1L);

        Mockito.when(taskService.getTaskById(anyLong())).thenReturn(task);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateTask() throws Exception {
        Long taskId = 1L;
        Task task = new Task();
        task.setDescription("Task description");
        task.setUserId(user.getId());
        task.setId(1L);

        Mockito.when(taskService.createTask(anyString(), anyString())).thenReturn(task);
        Mockito.when(userService.findUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Task description\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateTask() throws Exception {
        TaskDto taskDto = new TaskDto(7L, "test_106919a8a12c", true);
        Task task = new Task();
        task.setDescription("Task description");
        task.setUserId(user.getId());
        task.setId(7L);

        Mockito.when(taskService.getDto(Mockito.any(Task.class))).thenReturn(taskDto);
        Mockito.when(taskService.updateTask(any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/tasks")
                        .header("Authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"id\": 7,\n" +
                                "  \"description\": \"test_106919a8a12c\",\n" +
                                "  \"done\": true\n" +
                                "}")
                        .accept(MediaType.APPLICATION_JSON))//method not mocked
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeleteTask() throws Exception {
        Long taskId = 1L;
        Task task = new Task();
        task.setDescription("Task description");
        task.setUserId(user.getId());
        task.setId(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", taskId)
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
