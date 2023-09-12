package com.example.todo.web.controller;

import com.example.todo.persistence.model.User;
import com.example.todo.service.IUserService;
import com.example.todo.web.dto.LoginRequestDto;
import com.example.todo.web.util.GenericResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Andrew
 * @since 11.09.2023
 */
@RestController
@RequestMapping("/api")
public class UserRestController {
    @Autowired
    IUserService userService;


    @PostMapping("/user/login")
    public ResponseEntity<?> getToken(@NotNull final @RequestBody LoginRequestDto loginRequestDto) {
        System.out.println("LoginRequestDto: " + loginRequestDto.password() + " " + loginRequestDto.username());
        User user = userService.findUserByEmail(loginRequestDto.username());
        System.out.println("User: " + user);
        if (user != null) {
            return userService.getToken(user);
        } else {
            return ResponseEntity.badRequest().body(new GenericResponse("User not found"));
        }
    }
}
