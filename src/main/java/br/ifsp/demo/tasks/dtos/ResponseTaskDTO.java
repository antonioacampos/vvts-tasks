package br.ifsp.demo.tasks.dtos;

import br.ifsp.demo.tasks.Task;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseTaskDTO(
        String title,
        String description,
        LocalDateTime deadline,
        TaskStatus status,
        LocalDateTime startTime,
        LocalDateTime finishTime,
        Long timeSpent,
        Long estimatedTime,
        String suggestion,
        UUID userId
) {

    public ResponseTaskDTO(Task task){
        this(
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getStatus(),
                task.getStartTime(),
                task.getFinishTime(),
                task.getTimeSpent(),
                task.getEstimatedTime(),
                task.getSuggestion(),
                task.getUserId()
        );
    }

    public ResponseTaskDTO(TaskEntity task){
        this(
            task.getTitle(),
            task.getDescription(),
            task.getDeadline(),
            task.getStatus(),
            task.getStartTime(),
            task.getFinishTime(),
            task.getTimeSpent(),
            task.getEstimatedTime(),
            task.getSuggestion(),
            task.getUserId()
        );
    }

}
