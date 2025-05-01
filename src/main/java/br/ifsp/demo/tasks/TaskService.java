package br.ifsp.demo.tasks;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    private List<Task> tasks= new ArrayList<>();

    public Task createTask(String title, String description, LocalDateTime deadline){
        Task task = new Task(title, description, deadline);
        tasks.add(task);
        return task;
    }

    public Task editTask(int index, String anotherName, String anotherDescription, LocalDateTime localDateTime) {
        if (index >= tasks.size() || index < 0) throw new IndexOutOfBoundsException("Index out of bounds");

        Task task = tasks.get(index);

        task.setTitle(anotherName);
        task.setDescription(anotherDescription);
        task.setDeadline(localDateTime);

        return task;
    }

    public String getAllInformation() {
        return tasks.stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    public void deleteTask(int index) {
        if (index >= tasks.size() || index < 0) throw new IndexOutOfBoundsException("Index out of bounds");
        tasks.remove(index);
    }

    public Task getTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
        return tasks.get(index);
    }

    public void markAsCompleted(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
    
        Task task = tasks.get(index);
        task.markAsCompleted();
    }    

    public List<Task> filterByStatus(String statusString) {
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }
    
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void clockIn(int index, LocalDateTime startTime) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
        Task task = tasks.get(index);
        task.clockIn(startTime);
    }

    public void clockOut(int index, LocalDateTime finishTime) {
        Task task = tasks.get(index);
        task.clockOut(finishTime);
    }

    public long getSpentTime(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
        Task task = tasks.get(index);
        return task.getTimeSpent();
    }

    public boolean checkForTimeExceeded(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
        Task task = tasks.get(index);
        checkAndUpdateStatusForTimeExceeded(task);
        return task.getStatus() == TaskStatus.TIME_EXCEEDED;
    }

    private void checkAndUpdateStatusForTimeExceeded(Task task) {
        if (task.getStatus() == TaskStatus.IN_PROGRESS &&
                LocalDateTime.now().isAfter(task.getStartTime().plusMinutes(task.getEstimatedTime()))) {
            long timeExceeded = LocalDateTime.now().until(task.getStartTime().plusMinutes(task.getEstimatedTime()), ChronoUnit.MINUTES);
            long tolerance = (long) (task.getEstimatedTime() * 0.10);
            if (timeExceeded <= tolerance) {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
            } else {
                task.setStatus(TaskStatus.TIME_EXCEEDED);
                task.setSuggestion("Please re-evaluate the task.");
            }
        }
    }

    public String checkAndNotifyTimeExceeded(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task not found");
        }
        Task task = tasks.get(index);
        checkAndUpdateStatusForTimeExceeded(task);

        if (task.getStatus() == TaskStatus.TIME_EXCEEDED) {
            if (task.getSuggestion() != null) {
                return task.getSuggestion();
            }
            return "Time exceeded! Please register the clock-out.";
        }
        return "Task is within the estimated time.";
    }
}
