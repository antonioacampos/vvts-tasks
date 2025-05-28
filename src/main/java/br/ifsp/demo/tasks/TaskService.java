package br.ifsp.demo.tasks;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
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

    public Task editTask(UUID taskId, String anotherTitle, String anotherDescription, LocalDateTime deadline, UUID userId, LocalDateTime currentTime) {
        if(Objects.isNull(anotherTitle) || Objects.isNull(anotherDescription) || Objects.isNull(deadline) || Objects.isNull(userId) || Objects.isNull(currentTime))
            throw new NullPointerException();

        TaskEntity taskEntity = taskServiceDB.editTask(taskId, anotherTitle, anotherDescription, deadline, userId, currentTime);
        return convertToTask(taskEntity);
    }

    public String getAllInformation(UUID userId) {
        return taskServiceDB.getAllByUser(userId).stream()
                .map(this::convertToTask)
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    public void deleteTask(UUID taskId, UUID userId) {
        taskServiceDB.deleteTask(taskId, userId);
    }

    public Task getTask(UUID taskId, UUID userId) {
        TaskEntity taskEntity = taskServiceDB.getTask(taskId, userId);
        return convertToTask(taskEntity);
    }

    public Task markAsCompleted(UUID taskId, UUID userId) {
        TaskEntity taskEntity = taskServiceDB.markAsCompleted(taskId, userId);
        return convertToTask(taskEntity);
    }

    public List<Task> filterByStatus(String statusString, UUID userId) {
        return taskServiceDB.filterByStatus(statusString, userId).stream()
                .map(this::convertToTask)
                .collect(Collectors.toList());
    }

    public Task clockIn(UUID taskId, LocalDateTime startTime, UUID userId) {
        TaskEntity taskEntity = taskServiceDB.clockIn(taskId, startTime, userId);
        return convertToTask(taskEntity);
    }

    public Task clockOut(UUID taskId, LocalDateTime finishTime, UUID userId) {
        TaskEntity taskEntity = taskServiceDB.clockOut(taskId, finishTime, userId);
        return convertToTask(taskEntity);
    }

    public long getSpentTime(UUID taskId, UUID userId) {
        TaskEntity task = taskServiceDB.getTask(taskId, userId);
        return task.getTimeSpent();
    }

    public boolean checkForTimeExceeded(UUID taskId, UUID userId, LocalDateTime currentTime) {
        return taskServiceDB.checkForTimeExceeded(taskId, userId, currentTime);
    }

    public String checkAndNotifyTimeExceeded(UUID taskId, UUID userId, LocalDateTime currentTime) {
        return taskServiceDB.checkAndNotifyTimeExceeded(taskId, userId, currentTime);
    }

    public String checkForClockOutForgotten(UUID taskId, UUID userId, LocalDateTime currentTime) {
        return taskServiceDB.checkForClockOutForgotten(taskId, userId, currentTime);
    }

    public String checkForClockOutForgottenInCompletedTask(UUID taskId, UUID userId) {
        return taskServiceDB.checkForClockOutForgottenInCompletedTask(taskId, userId);
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
