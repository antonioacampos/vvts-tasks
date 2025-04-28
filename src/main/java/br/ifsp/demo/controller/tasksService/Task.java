package br.ifsp.demo.controller.tasksService;

import java.time.LocalDateTime;

public class Task {

    private String name;
    private String description;
    private LocalDateTime deadline;

    public Task(String name, String description, LocalDateTime deadline) {
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
}
