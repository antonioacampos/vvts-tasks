package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskMutationServiceDBTest {

    @Autowired
    private TaskServiceDB taskServiceDB;

    @Autowired
    private JpaTaskRepository repository;

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnFalseWhenTimeNotExceeded() {
        UUID userId = UUID.randomUUID();

        // estimated time = 100min -> tolerance = 10min
        CreateTaskDTO dto = new CreateTaskDTO("Tarefa A", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);

        // clockIn agora
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // currentTime = start + 105min (ainda está dentro da tolerância de 110min)
        LocalDateTime currentTime = task.getStartTime().plusMinutes(105);

        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, currentTime);

        // Esperado: ainda não deve ter excedido
        assertTrue(result);
        assertNull(task.getSuggestion());
    }
}
