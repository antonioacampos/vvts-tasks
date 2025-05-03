package br.ifsp.demo.tasks.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateTaskDTO(
        @NotBlank String title,
        String description,
        @Future LocalDateTime deadline
) {
}
