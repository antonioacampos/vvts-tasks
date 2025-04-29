package br.ifsp.demo.controller.tasksService;

import java.time.LocalDateTime;

public class Task {

    private String name;
    private String description;
    private LocalDateTime deadline;

    public Task(String name, String description, LocalDateTime deadline) {

        if(name.isBlank()) throw new IllegalArgumentException("Cannot create task with blank title");

        if(deadline.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot create task with outdated deadline");

        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setName(String name) {
        if(name.isBlank()) throw new IllegalArgumentException("Cannot edit task with blank title");
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDateTime deadline) {
        if(deadline.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Cannot edit task with outdated deadline");
        this.deadline = deadline;
    }
}
