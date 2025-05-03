package br.ifsp.demo.tasks.dtos;

import br.ifsp.demo.tasks.Task;

import java.time.LocalDateTime;

public record ResponseTaskDTO(
        //UUID id,
        String title,
        String description,
        LocalDateTime deadline
) {

    public ResponseTaskDTO(Task task){
        this(
                task.getTitle(),
                task.getDescription(),
                task.getDeadline()
        );
    }

}
