package br.ifsp.demo.tasks;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Task {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status = TaskStatus.PENDING;

    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Long timeSpent;
    private Long estimatedTime;

    private String suggestion;

    private UUID userId;

    public Task(UUID id, String title, String description, LocalDateTime deadline, UUID userId) {

        if(title.isBlank()) throw new IllegalArgumentException("Cannot create task with blank title");

        if(deadline.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot create task with outdated deadline");

        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.userId = userId;
    }

    public UUID getId() {return id;}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setTitle(String title) {
        if(title.isBlank()) throw new IllegalArgumentException("Cannot edit task with blank title");
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDateTime deadline) {
        if(deadline.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot edit task with outdated deadline");
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        return String.format(
                "{\n  \"title\": \"%s\",\n  \"description\": \"%s\",\n  \"deadline\": \"%s\"\n}",
                this.title,
                this.description,
                this.deadline.format(formatter)
        );
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void markAsCompleted() {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in progress to be marked as completed");
        }
        this.status = TaskStatus.COMPLETED;
    }

    public void clockIn(LocalDateTime startTime) {

        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("Only pending tasks can be started");
        }

        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        if (startTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the future");
        }

        this.startTime = startTime;
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }
    
    public void setEstimatedTime(Long estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public void clockOut(LocalDateTime finishTime) {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in progress to be clocked out");
        }
        this.finishTime = finishTime;
        this.timeSpent = startTime.until(finishTime, java.time.temporal.ChronoUnit.MINUTES);
        this.status = TaskStatus.COMPLETED;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }


    public Long getEstimatedTime() {
        return estimatedTime;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public UUID getUserId() {
        return userId;
    }
}
