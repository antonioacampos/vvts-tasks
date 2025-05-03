package br.ifsp.demo.tasks.dtos;

import java.time.LocalDateTime;

public record ResponseTaskDTO(
        //UUID id,
        String title,
        String description,
        LocalDateTime deadline
) {
}
