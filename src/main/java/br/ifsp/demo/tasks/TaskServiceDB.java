package br.ifsp.demo.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceDB {

    private final JpaTaskRepository repository;

    public TaskEntity create(String title, String description, LocalDateTime deadline, long estimatedTime, UUID userId) {
        TaskEntity task = TaskEntity.builder()
        .title(title)
        .description(description)
        .deadline(deadline)
        .estimatedTime(estimatedTime)
        .userId(userId)
        .status(TaskStatus.PENDING)
        .build();
        return repository.save(task);
    }

    public List<TaskEntity> getAllByUser(UUID userId) {
        return repository.findAllByUserId(userId);
    }

    public TaskEntity getByIdAndUser(UUID id, UUID userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(
            () -> new IllegalArgumentException("Task not found")
        );
    }
    
    public TaskEntity clockIn(UUID taskId, UUID userId) {
        TaskEntity task = getByIdAndUser(taskId, userId);
    
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Only PENDING tasks can be started.");
        }
    
        task.setStartTime(LocalDateTime.now());
        task.setStatus(TaskStatus.IN_PROGRESS);
    
        return repository.save(task);
    }    
}
