package com.example.todo.web.controller;


import com.example.todo.service.ITaskService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

/**
 * @author Andrew
 * @since 07.09.2023
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final ITaskService ITaskService;

    public TaskController(ITaskService ITaskService) {
        this.ITaskService = ITaskService;
    }

    @GetMapping("/")
    public ModelAndView getTask() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal().equals("anonymousUser")) {
            return new ModelAndView("redirect:/login");
        }
        String username = ((User) authentication.getPrincipal()).getUsername();
        ModelAndView modelAndView = new ModelAndView("todo");
        // Add model attributes if needed
        modelAndView.addObject("tasks", ITaskService.getAllTasksByUser(username));
        modelAndView.addObject("time", LocalDate.now(LocaleContextHolder.getTimeZone().toZoneId()));
        return modelAndView;
    }
}
