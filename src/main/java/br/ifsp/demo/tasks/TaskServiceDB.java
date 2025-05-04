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

    public TaskEntity clockOut(UUID taskId, UUID userId) {
        TaskEntity task = getByIdAndUser(taskId, userId);
    
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS tasks can be finished");
        }
    
        LocalDateTime finishTime = LocalDateTime.now();
        long timeSpent = java.time.Duration.between(task.getStartTime(), finishTime).toMinutes();
    
        task.setFinishTime(finishTime);
        task.setTimeSpent(timeSpent);
        task.setStatus(TaskStatus.COMPLETED);

        return repository.save(task);
    }

    public TaskEntity getTask(UUID taskId, UUID userId) {
        return repository.findByIdAndUserId(taskId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }    
    
    public TaskEntity editTask(UUID taskId, String title, String description, LocalDateTime deadline, UUID userId) {
        TaskEntity task = getTask(taskId, userId);
    
        if (title.isBlank()) {
            throw new IllegalArgumentException("Cannot edit task with blank title");
        }
    
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot edit task with outdated deadline");
        }
    
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
    
        return repository.save(task);
    }

    public void deleteTask(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);
        repository.delete(task);
    }

    public TaskEntity markAsCompleted(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);
    
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in progress to be marked as completed");
        }
    
        task.setStatus(TaskStatus.COMPLETED);
        return repository.save(task);
    }
    
}
