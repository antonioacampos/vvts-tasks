package br.ifsp.demo.tasks;

import br.ifsp.demo.security.auth.*;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerDBTest {

    @Autowired private JpaUserRepository userRepository;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TaskServiceDB taskService;
    @Autowired private AuthenticationService authService;

    private String token;
    private UUID userId;

    @BeforeEach
    void setup() {
        String email = "test@example.com";
        String password = "123456";
    
        try {
            authService.register(new RegisterUserRequest("Test", "User", email, password));
        } catch (Exception ignored) {}
    
        AuthResponse response = authService.authenticate(new AuthRequest(email, password));
        token = "Bearer " + response.token();
    
        // ⚠️ Corrigido: buscar o user manualmente no banco ao invés de usar o SecurityContext
        userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"))
                .getId();
    }
    

    private CreateTaskDTO exampleDTO() {
        return new CreateTaskDTO("Estudar Spring", "Segurança JWT",
                LocalDateTime.now().plusHours(1), 60, null);
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/task/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Estudar Spring"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        TaskEntity task = taskService.create("Ler livro", "Capítulo 1",
                LocalDateTime.now().plusHours(2), 30, userId);

        mockMvc.perform(get("/api/v1/task/get/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ler livro"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/v1/task/get-all")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldEditTaskSuccessfully() throws Exception {
        TaskEntity task = taskService.create("Tarefa Antiga", "Desc",
                LocalDateTime.now().plusDays(1), 45, userId);

        CreateTaskDTO updated = new CreateTaskDTO("Tarefa Editada", "Nova desc",
                LocalDateTime.now().plusDays(2), 90, null);

        mockMvc.perform(put("/api/v1/task/edit/" + task.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Editada"));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        TaskEntity task = taskService.create("Para deletar", "Desc",
                LocalDateTime.now().plusDays(1), 40, userId);

        mockMvc.perform(delete("/api/v1/task/delete/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldClockInAndOutSuccessfully() throws Exception {
        TaskEntity task = taskService.create("Clock Test", "Desc",
                LocalDateTime.now().plusHours(1), 30, userId);

        mockMvc.perform(put("/api/v1/task/clock-in/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/v1/task/clock-out/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldMarkAsCompleted() throws Exception {
        TaskEntity task = taskService.create("Para Completar", "Desc",
                LocalDateTime.now().plusHours(1), 20, userId);
        taskService.clockIn(task.getId(), userId);

        mockMvc.perform(put("/api/v1/task/mark-completed/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnSpentTime() throws Exception {
        TaskEntity task = taskService.create("Spent", "time",
                LocalDateTime.now().plusHours(1), 20, userId);
        taskService.clockIn(task.getId(), userId);
        taskService.clockOut(task.getId(), userId);

        mockMvc.perform(get("/api/v1/task/spent-time/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldNotifyTimeExceeded() throws Exception {
        TaskEntity task = taskService.create("Time Exceeded", "Desc",
                LocalDateTime.now().plusHours(1), 1, userId);
        task.setStartTime(LocalDateTime.now().minusMinutes(10));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task);

        mockMvc.perform(get("/api/v1/task/notify-time-exceeded/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldCheckTimeExceeded() throws Exception {
        TaskEntity task = taskService.create("Check Exceeded", "Teste",
                LocalDateTime.now().plusHours(1), 5, userId);
        task.setStartTime(LocalDateTime.now().minusMinutes(15));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task);

        mockMvc.perform(get("/api/v1/task/check-time-exceeded/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void shouldCheckClockOutForgotten() throws Exception {
        TaskEntity task = taskService.create("ClockOut Forgotten", "Teste",
                LocalDateTime.now().plusHours(1), 5, userId);
        task.setStartTime(LocalDateTime.now().minusMinutes(15));
        task.setFinishTime(null);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task);

        mockMvc.perform(get("/api/v1/task/clock-out-forgotten/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("You forgot to clock out. Please register the clock-out."));
    }

    @Test
    void shouldCheckClockOutForgottenCompletedTask() throws Exception {
        TaskEntity task = taskService.create("Completed no finishTime", "Teste",
                LocalDateTime.now().plusHours(1), 5, userId);
        task.setStartTime(LocalDateTime.now().minusMinutes(10));
        task.setFinishTime(null);
        task.setStatus(TaskStatus.COMPLETED);
        taskService.updateTask(task);

        mockMvc.perform(get("/api/v1/task/clock-out-forgotten-completed-task/" + task.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Clock-out is no longer necessary as the task is already completed."));
    }
}
