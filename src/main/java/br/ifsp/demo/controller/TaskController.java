package br.ifsp.demo.controller;

import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.tasks.Task;
import br.ifsp.demo.tasks.TaskService;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
@Tag(name = "Task API")
public class TaskController {

    private final AuthenticationInfoService authenticationInfoService;
    private final TaskService taskService = new TaskService();


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTaskDTO task){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        Task newTask = taskService.createTask(task.title(), task.description(), task.deadline());

        ResponseTaskDTO response = new ResponseTaskDTO(
                newTask.getTitle(),
                newTask.getDescription(),
                newTask.getDeadline()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        String response = taskService.getAllInformation();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
