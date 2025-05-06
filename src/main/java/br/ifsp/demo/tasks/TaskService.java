package br.ifsp.demo.tasks;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskService {

    private List<Task> tasks= new ArrayList<>();

    public Task createTask(String title, String description, LocalDateTime deadline, UUID userId) {
        if(Objects.isNull(title) || Objects.isNull(description) || Objects.isNull(deadline) || Objects.isNull(userId))
            throw new NullPointerException();

        Task task = new Task(title, description, deadline, userId);
        tasks.add(task);
        return task;
    }

    public Task editTask(int index, String anotherName, String anotherDescription, LocalDateTime localDateTime, UUID userId) {
        Task task = findTaskByUserId(userId, index);

        task.setTitle(anotherName);
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

    public String checkAndNotifyTimeExceeded(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);
        checkAndUpdateStatusForTimeExceeded(task);

        if (task.getStatus() == TaskStatus.TIME_EXCEEDED) {
            if (task.getSuggestion() != null) {
                return task.getSuggestion();
            }
            return "Time exceeded! Please register the clock-out.";
        }
        return "Task is within the estimated time.";
    }

    public String checkForClockOutForgotten(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);

        if (task.getStatus() == TaskStatus.IN_PROGRESS &&
                LocalDateTime.now().isAfter(task.getStartTime().plusMinutes(task.getEstimatedTime())) &&
                task.getFinishTime() == null) {
            return "You forgot to clock out. Please register the clock-out.";
        }
        return "Task is within the estimated time or clock-out is already registered.";
    }

    public String checkForClockOutForgottenInCompletedTask(int index, UUID userId) {
        Task task = findTaskByUserId(userId, index);

        if (task.getStatus() == TaskStatus.COMPLETED && task.getFinishTime() == null) {
            return "Clock-out is no longer necessary as the task is already completed.";
        }

        return "Clock-out is not forgotten or the task is not completed.";
    }

    private Task findTaskByUserId(UUID userId, int index) {
        List<Task> userTasks = tasks.stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());

        if (index < 0 || index >= userTasks.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        return userTasks.get(index);
    }
}
