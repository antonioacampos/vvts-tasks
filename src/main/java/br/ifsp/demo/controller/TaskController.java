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

        TaskEntity newTask = taskService.create(
                new CreateTaskDTO(task.title(), 
                task.description(), task.deadline(), task.estimatedTime(), null), userId);

        return new ResponseEntity<>(new ResponseTaskDTO(newTask), HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable UUID id, @RequestBody CreateTaskDTO task) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity edited = taskService.editTask(
                id, task.title(), task.description(), task.deadline(), userId);

        return new ResponseEntity<>(new ResponseTaskDTO(edited), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        List<ResponseTaskDTO> response = taskService.getAllByUser(userId).stream()
                .map(ResponseTaskDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        TaskEntity task = taskService.getTask(id, userId);
        return new ResponseEntity<>(new ResponseTaskDTO(task), HttpStatus.OK);
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

        taskService.markAsCompleted(id, userId);
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
        return new ResponseEntity<>(Map.of("status", task.getTimeSpent()), HttpStatus.OK);
    }

    @GetMapping("/check-time-exceeded/{id}")
    public ResponseEntity<?> checkTimeExceeded(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        boolean exceeded = taskService.checkForTimeExceeded(id, userId);
        return new ResponseEntity<>(Map.of("status", exceeded), HttpStatus.OK);
    }

    @GetMapping("/notify-time-exceeded/{id}")
    public ResponseEntity<?> notifyTimeExceeded(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        String notify = taskService.checkAndNotifyTimeExceeded(id, userId);
        return new ResponseEntity<>(Map.of("status", notify), HttpStatus.OK);
    }

    @GetMapping("/clock-out-forgotten/{id}")
    public ResponseEntity<?> clockOutForgotten(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        String check = taskService.checkForClockOutForgotten(id, userId);
        return new ResponseEntity<>(Map.of("status", check), HttpStatus.OK);
    }

    @GetMapping("/clock-out-forgotten-completed-task/{id}")
    public ResponseEntity<?> clockOutForgottenCompletedTask(@PathVariable UUID id) {
        final UUID userId = authenticationInfoService.getAuthenticatedUserId();

        String check = taskService.checkForClockOutForgottenInCompletedTask(id, userId);
        return new ResponseEntity<>(Map.of("status", check), HttpStatus.OK);
    }
}
