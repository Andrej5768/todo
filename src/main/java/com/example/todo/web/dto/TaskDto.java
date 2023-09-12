package com.example.todo.web.dto;

/**
 * @author Andrew
 * @since 08.09.2023
 */
public record TaskDto(Long id, String description, boolean done) {
}
