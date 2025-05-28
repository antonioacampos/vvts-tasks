package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceDB {

    private final JpaTaskRepository repository;

    public TaskEntity create(CreateTaskDTO createTaskDTO, UUID userId) {
        TaskEntity task = TaskEntity.builder()
                .title(createTaskDTO.title())
                .description(createTaskDTO.description())
                .deadline(createTaskDTO.deadline())
                .estimatedTime(createTaskDTO.estimatedTime())
                .userId(userId)
                .status(TaskStatus.PENDING)
                .suggestion(createTaskDTO.suggestion())
                .build();
        return repository.save(task);
    }

    public TaskEntity updateTask(TaskEntity task) {
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

    public TaskEntity clockIn(UUID taskId, LocalDateTime startTime, UUID userId) {
        TaskEntity task = getByIdAndUser(taskId, userId);

        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        if (startTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the future");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Only PENDING tasks can be started.");
        }

        task.setStartTime(startTime);
        task.setStatus(TaskStatus.IN_PROGRESS);

        return repository.save(task);
    }

    public TaskEntity clockOut(UUID taskId, LocalDateTime finishTime, UUID userId) {
        TaskEntity task = getByIdAndUser(taskId, userId);

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS tasks can be finished");
        }

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

    public List<TaskEntity> filterByStatus(String statusString, UUID userId) {
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }

        return repository.findAllByUserId(userId).stream()
                .filter(task -> task.getStatus() == status)
                .toList();
    }

    public boolean checkForTimeExceeded(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            long timeExceeded = java.time.Duration.between(task.getStartTime(), LocalDateTime.now()).toMinutes();
            long tolerance = (long) (task.getEstimatedTime() * 0.10);
            if (timeExceeded > task.getEstimatedTime() + tolerance) {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion("Please re-evaluate or adjust the task.");
            } else {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion(null);
            }
            repository.save(task);
        }

        return task.getStatus() == TaskStatus.TIME_EXCEEDED;
    }

    public String checkAndNotifyTimeExceeded(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            long timeExceeded = java.time.Duration.between(task.getStartTime(), LocalDateTime.now()).toMinutes();
            long tolerance = (long) (task.getEstimatedTime() * 0.10);

            if (timeExceeded > task.getEstimatedTime() + tolerance) {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion("Please re-evaluate or adjust the task.");
            } else {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion(null);
            }

            repository.save(task);
        }

        if (task.getStatus() == TaskStatus.TIME_EXCEEDED) {
            return task.getSuggestion() != null
                    ? task.getSuggestion()
                    : "Time exceeded! Please register the clock-out.";
        }

        return "Task is within the estimated time.";
    }

    public String checkForClockOutForgotten(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);

        if (task.getStatus() == TaskStatus.IN_PROGRESS &&
                LocalDateTime.now().isAfter(task.getStartTime().plusMinutes(task.getEstimatedTime())) &&
                task.getFinishTime() == null) {
            return "You forgot to clock out. Please register the clock-out.";
        }

        return "Task is within the estimated time or clock-out is already registered.";
    }

    public String checkForClockOutForgottenInCompletedTask(UUID id, UUID userId) {
        TaskEntity task = getByIdAndUser(id, userId);

        if (task.getStatus() == TaskStatus.COMPLETED && task.getFinishTime() == null) {
            return "Clock-out is no longer necessary as the task is already completed.";
        }

        return "Clock-out is not forgotten or the task is not completed.";
    }
}
