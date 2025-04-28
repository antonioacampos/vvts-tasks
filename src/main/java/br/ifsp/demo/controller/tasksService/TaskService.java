package br.ifsp.demo.controller.tasksService;

import java.time.LocalDateTime;

public class TaskService {

    public Task createTask(String name, String description, LocalDateTime deadline){
        return new Task(name, description, deadline);
    }

}
