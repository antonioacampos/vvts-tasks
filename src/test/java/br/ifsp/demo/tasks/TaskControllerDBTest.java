package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import br.ifsp.demo.security.auth.AuthenticationInfoService;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskServiceDB taskService;

    @Autowired
    private AuthenticationInfoService authService;

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();

        CreateTaskDTO dto = new CreateTaskDTO(
                "Estudar Spring",
                "Focar no módulo de segurança",
                LocalDateTime.now().plusHours(1),
                60,
                null
        );

        mockMvc.perform(post("/api/v1/task/create")
                        .header("Authorization", "Bearer " + "<token_jwt_aqui>")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Estudar Spring"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnTaskById() throws Exception {
        UUID userId = authService.getAuthenticatedUserId();

        TaskEntity task = taskService.create(
                "Ler livro",
                "Capítulo 1",
                LocalDateTime.now().plusHours(2),
                30,
                userId
        );

        mockMvc.perform(get("/api/v1/task/get/" + task.getId())
                        .header("Authorization", "Bearer " + "<token_jwt_aqui>")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Ler livro"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
