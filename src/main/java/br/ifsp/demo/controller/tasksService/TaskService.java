package br.ifsp.demo.controller.tasksService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private List<Task> tasks= new ArrayList<>();

    public Task createTask(String title, String description, LocalDateTime deadline){
        Task task = new Task(title, description, deadline);
        tasks.add(task);
        return task;
    }

    public Task editTask(int index, String anotherName, String anotherDescription, LocalDateTime localDateTime) {
        if(index > tasks.size()) throw new IndexOutOfBoundsException("Index out of bounds");

        Task task = tasks.get(index);

        task.setTitle(anotherName);
        task.setDescription(anotherDescription);
        task.setDeadline(localDateTime);

        return task;
    }
}
