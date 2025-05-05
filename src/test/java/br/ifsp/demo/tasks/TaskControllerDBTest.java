package br.ifsp.demo.tasks;

import br.ifsp.demo.security.auth.AuthenticationInfoService;
import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TaskControllerDBTest.MockAuthConfig.class)
class TaskControllerDBTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskServiceDB taskService;

    @Autowired
    private AuthenticationInfoService authService;

    // Substitua pelo token válido ao rodar os testes
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ2NDc1MzE3LCJleHAiOjE3NDY0ODM5NTd9.I29A0paRdKkTbSV6aIlh7O4KSsGCTwk7Br0WrwmpzEs";

    private CreateTaskDTO exampleDTO() {
        return new CreateTaskDTO(
                "Estudar Spring",
                "Segurança JWT",
                LocalDateTime.now().plusHours(1),
                60,
                null
        );
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/task/create")
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Estudar Spring"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Ler livro", "Capítulo 1", LocalDateTime.now().plusHours(2), 30, userId);

        mockMvc.perform(get("/api/v1/task/get/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ler livro"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/v1/task/get-all")
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void shouldEditTaskSuccessfully() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Tarefa Antiga", "Desc", LocalDateTime.now().plusDays(1), 45, userId);

        CreateTaskDTO updated = new CreateTaskDTO("Tarefa Editada", "Nova desc", LocalDateTime.now().plusDays(2), 90, null);

        mockMvc.perform(put("/api/v1/task/edit/" + task.getId())
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Editada"));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Para deletar", "Desc", LocalDateTime.now().plusDays(1), 40, userId);

        mockMvc.perform(delete("/api/v1/task/delete/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldClockInAndOutSuccessfully() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Clock Test", "Desc", LocalDateTime.now().plusHours(1), 30, userId);

        mockMvc.perform(put("/api/v1/task/clock-in/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/v1/task/clock-out/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldMarkAsCompleted() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Para Completar", "Desc", LocalDateTime.now().plusHours(1), 20, userId);
        taskService.clockIn(task.getId(), userId);

        mockMvc.perform(put("/api/v1/task/mark-completed/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnSpentTime() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Spent", "time", LocalDateTime.now().plusHours(1), 20, userId);
        taskService.clockIn(task.getId(), userId);
        taskService.clockOut(task.getId(), userId);

        mockMvc.perform(get("/api/v1/task/spent-time/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldNotifyTimeExceeded() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Time Exceeded", "Desc", LocalDateTime.now().plusHours(1), 1, userId);
        task.setStartTime(LocalDateTime.now().minusMinutes(10));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.getAllByUser(userId);

        mockMvc.perform(get("/api/v1/task/notify-time-exceeded/" + task.getId())
                        .header(AUTH_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldCheckTimeExceeded() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Check Exceeded", "Teste", LocalDateTime.now().plusHours(1), 5, userId);
    
        task.setStartTime(LocalDateTime.now().minusMinutes(15)); // tempo já excedido
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task); // <- garante que está persistido
    
        mockMvc.perform(get("/api/v1/task/check-time-exceeded/" + task.getId())
                .header(AUTH_HEADER, TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(true));
    }
    
    @Test
    void shouldCheckClockOutForgotten() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("ClockOut Forgotten", "Teste", LocalDateTime.now().plusHours(1), 5, userId);
    
        task.setStartTime(LocalDateTime.now().minusMinutes(15)); // antes do tempo estimado
        task.setFinishTime(null); // clock-out não registrado
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateTask(task); // <- salva alterações no banco
    
        mockMvc.perform(get("/api/v1/task/clock-out-forgotten/" + task.getId())
                .header(AUTH_HEADER, TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("You forgot to clock out. Please register the clock-out."));
    }    
    
    @Test
    void shouldCheckClockOutForgottenCompletedTask() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();
        TaskEntity task = taskService.create("Completed no finishTime", "Teste", LocalDateTime.now().plusHours(1), 5, userId);
    
        task.setStartTime(LocalDateTime.now().minusMinutes(10));
        task.setFinishTime(null);
        task.setStatus(TaskStatus.COMPLETED);
        taskService.updateTask(task); // <- salva no banco
    
        mockMvc.perform(get("/api/v1/task/clock-out-forgotten-completed-task/" + task.getId())
                .header(AUTH_HEADER, TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Clock-out is no longer necessary as the task is already completed."));
    }

    @TestConfiguration
    static class MockAuthConfig {
        @Bean
        public AuthenticationInfoService authService() {
            return new AuthenticationInfoService() {
                @Override
                public UUID getAuthenticatedUserId() {
                    return UUID.fromString("ce30f00a-38b3-419d-a68c-b5ee4e91c03f");
                }
            };
        }
    }
    
}