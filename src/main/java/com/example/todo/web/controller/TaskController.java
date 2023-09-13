package com.example.todo.web.controller;

import org.springframework.context.i18n.LocaleContextHolder;
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

    @GetMapping("/")
    public ModelAndView getTask() {
        ModelAndView modelAndView = new ModelAndView("todo");
        modelAndView.addObject("time", LocalDate.now(LocaleContextHolder.getTimeZone().toZoneId()));
        return modelAndView;
    }
}
