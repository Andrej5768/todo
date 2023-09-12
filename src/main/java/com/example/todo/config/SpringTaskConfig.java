package com.example.todo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Andrew
 * @since 06.09.2023
 */
@Configuration
@EnableScheduling
@ComponentScan({"com.example.todo.task"})
public class SpringTaskConfig {

}
