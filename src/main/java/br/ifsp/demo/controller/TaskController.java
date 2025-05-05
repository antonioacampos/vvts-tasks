package br.ifsp.demo.controller;

import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.tasks.TaskEntity;
import br.ifsp.demo.tasks.TaskServiceDB;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
@Tag(name = "Task API")
public class TaskController {

    private final AuthenticationInfoService authenticationInfoService;
    private final TaskServiceDB taskService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTaskDTO task) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity newTask = taskService.create(task.title(), task.description(), task.deadline(), task.estimatedTime(), userId);

        ResponseTaskDTO response = new ResponseTaskDTO(newTask);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable UUID id, @RequestBody CreateTaskDTO task) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity edited = taskService.editTask(id, task.title(), task.description(), task.deadline(), userId);

        ResponseTaskDTO response = new ResponseTaskDTO(edited);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        List<TaskEntity> tasks = taskService.getAllByUser(userId);
        List<ResponseTaskDTO> response = tasks.stream().map(ResponseTaskDTO::new).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity task = taskService.getTask(id, userId);
        ResponseTaskDTO response = new ResponseTaskDTO(task);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        taskService.deleteTask(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/mark-completed/{id}")
    public ResponseEntity<?> markCompleted(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        taskService.clockOut(id, userId); // marcando como completed = clockOut
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/clock-in/{id}")
    public ResponseEntity<?> clockIn(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        taskService.clockIn(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/clock-out/{id}")
    public ResponseEntity<?> clockOut(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        taskService.clockOut(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/spent-time/{id}")
    public ResponseEntity<?> spentTime(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity task = taskService.getTask(id, userId);
        Map<String, Long> response = Map.of("status", task.getTimeSpent());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
