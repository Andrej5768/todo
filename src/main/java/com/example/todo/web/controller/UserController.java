package com.example.todo.web.controller;

import com.example.todo.security.ActiveUserStore;
import com.example.todo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Locale;

/**
 * @author Andrew
 * @since 08.09.2023
 */
@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @GetMapping("/loggedUsers")
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @GetMapping("/loggedUsersFromSessionRegistry")
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }

    @GetMapping("/registration")
    public String registrationForm() {
        return "registration";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/user/forgetPassword")
    public String forgetPassword() {
        return "forgetPassword";
    }

    @GetMapping("/successRegister")
    public String successRegister() {
        return "successRegister";
    }

    @GetMapping("/emailError")
    public String emailError() {
        return "emailError";
    }

}
