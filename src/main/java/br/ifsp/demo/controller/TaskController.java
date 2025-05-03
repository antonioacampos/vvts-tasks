package br.ifsp.demo.controller;

import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.tasks.Task;
import br.ifsp.demo.tasks.TaskService;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.tasks.dtos.ResponseTaskDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
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
    private final TaskService taskService = new TaskService();


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTaskDTO task){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        Task newTask = taskService.createTask(task.title(), task.description(), task.deadline());

        ResponseTaskDTO response = new ResponseTaskDTO(newTask);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable int id, @RequestBody CreateTaskDTO task){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        Task editedTask = taskService.editTask(id, task.title(), task.description(), task.deadline());

        ResponseTaskDTO response = new ResponseTaskDTO(editedTask);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        String response = taskService.getAllInformation();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        taskService.deleteTask(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> get(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        Task task = taskService.getTask(id);

        ResponseTaskDTO response = new ResponseTaskDTO(task);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/mark-completed/{id}")
    public ResponseEntity<?> markCompletec(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        taskService.markAsCompleted(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-by-status")
    public ResponseEntity<?> getByStatus(@RequestParam(required = true) String status){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        List<Task> tasks = taskService.filterByStatus(status);

        List<ResponseTaskDTO> response = tasks.stream()
                .map(ResponseTaskDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/clock-in/{id}")
    public ResponseEntity<?> clockIn(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        taskService.clockIn(id, LocalDateTime.now());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/clock-out/{id}")
    public ResponseEntity<?> clockOut(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        taskService.clockOut(id, LocalDateTime.now());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/spent-time/{id}")
    public ResponseEntity<?> spentTime(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        long time = taskService.getSpentTime(id);

        Map<String, Long> response = Map.of("status", time);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("check-time-exceeded/{id}")
    public ResponseEntity<?> checkTimeExceeded(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        boolean exceeded = taskService.checkForTimeExceeded(id);

        Map<String, Boolean> response = Map.of("status", exceeded);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/notify-time-exceeded/{id}")
    public ResponseEntity<?> notifyTimeExceeded(@PathVariable int id){
        final UUID userID = authenticationInfoService.getAuthenticatedUserId();

        String notify = taskService.checkAndNotifyTimeExceeded(id);

        Map<String, String> response = Map.of("status", notify);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
