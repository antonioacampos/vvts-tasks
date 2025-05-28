package br.ifsp.demo.tasks;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TaskService {

    private final TaskServiceDB taskServiceDB;

    public Task createTask(String title, String description, LocalDateTime deadline, long estimatedTime, UUID userId) {
        if(Objects.isNull(title) || Objects.isNull(description) || Objects.isNull(deadline) || Objects.isNull(userId))
            throw new NullPointerException();

        CreateTaskDTO createTaskDTO = new CreateTaskDTO(title, description, deadline, estimatedTime, null);
        TaskEntity taskEntity = taskServiceDB.create(createTaskDTO, userId);
        return convertToTask(taskEntity);
    }

    public Task editTask(int index, String anotherTitle, String anotherDescription, LocalDateTime localDateTime, UUID userId) {
        if(Objects.isNull(anotherTitle) || Objects.isNull(anotherDescription) || Objects.isNull(localDateTime) || Objects.isNull(userId))
            throw new NullPointerException();
        Task task = findTaskByUserId(userId, index);

        task.setTitle(anotherTitle);
        task.setDescription(anotherDescription);
        task.setDeadline(localDateTime);

        return task;
    }

    public String getAllInformation(UUID userId) {
        return tasks.stream()
                .filter(task -> task.getUserId().equals(userId))
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    public void deleteTask(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        tasks.remove(task);
    }

    public Task getTask(int index, UUID userId) {
        return findTaskByUserId(userId, index);
    }

    public void markAsCompleted(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        task.markAsCompleted();
    }

    public List<Task> filterByStatus(String statusString, UUID userId) {
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }

        return tasks.stream()
                .filter(task -> task.getUserId().equals(userId) && task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void clockIn(int index, LocalDateTime startTime, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        task.clockIn(startTime);
    }

    public void clockOut(int index, LocalDateTime finishTime, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        task.clockOut(finishTime);
    }

    public long getSpentTime(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        return task.getTimeSpent();
    }

    public boolean checkForTimeExceeded(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        checkAndUpdateStatusForTimeExceeded(task);
        return task.getStatus() == TaskStatus.TIME_EXCEEDED;
    }

    private void checkAndUpdateStatusForTimeExceeded(Task task) {
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            long timeExceeded = ChronoUnit.MINUTES.between(task.getStartTime(), LocalDateTime.now());
            long tolerance = (long) (task.getEstimatedTime() * 0.10);
            if (timeExceeded > task.getEstimatedTime() + tolerance) {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion("Please re-evaluate or adjust the task.");
            } else {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion(null);
            }
        }
    }

    private Task convertToTask(TaskEntity taskEntity) {
        Task task = new Task(
                taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getDeadline(),
                taskEntity.getUserId()
        );
        task.setStatus(taskEntity.getStatus());

        if (taskEntity.getStartTime() != null) {
            task.setStartTime(taskEntity.getStartTime());
        }

        if (taskEntity.getFinishTime() != null) {
            task.setFinishTime(taskEntity.getFinishTime());
        }

        if (taskEntity.getTimeSpent() != null) {
            task.setTimeSpent(taskEntity.getTimeSpent());
        }

        if (taskEntity.getEstimatedTime() != null) {
            task.setEstimatedTime(taskEntity.getEstimatedTime());
        }

        if (taskEntity.getSuggestion() != null) {
            task.setSuggestion(taskEntity.getSuggestion());
        }

        return task;
    }

}
