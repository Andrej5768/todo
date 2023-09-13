package com.example.todo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Andrew
 * @since 08.09.2023
 */
@Schema(description = "Task data transfer object")
public record TaskDto(Long id, String description, boolean done) {
}
