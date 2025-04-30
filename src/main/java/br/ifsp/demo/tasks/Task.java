package br.ifsp.demo.tasks;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private String title;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status = TaskStatus.PENDING;

    public Task(String title, String description, LocalDateTime deadline) {

        if(title.isBlank()) throw new IllegalArgumentException("Cannot create task with blank title");

        if(deadline.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot create task with outdated deadline");

        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

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
    
}
