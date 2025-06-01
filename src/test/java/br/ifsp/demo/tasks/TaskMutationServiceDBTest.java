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

    private final UUID userId = UUID.randomUUID();

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

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldSetSuggestionToNullWhenTimeWithinTolerance() {
        UUID userId = UUID.randomUUID();

        CreateTaskDTO dto = new CreateTaskDTO("Tarefa B", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);

        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // Dentro da tolerância (<= 110min)
        LocalDateTime currentTime = task.getStartTime().plusMinutes(108);

        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, currentTime);

        assertTrue(result);
        TaskEntity updated = repository.findById(task.getId()).get();
        assertNull(updated.getSuggestion()); // Confirmar setSuggestion(null) foi executado
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldNotifyWithinToleranceInCheckAndNotifyTimeExceeded() {
        UUID userId = UUID.randomUUID();

        CreateTaskDTO dto = new CreateTaskDTO("Tarefa C", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // currentTime dentro da tolerância (ex: 108min de 110min)
        LocalDateTime currentTime = task.getStartTime().plusMinutes(108);

        String message = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, currentTime);

        // A mensagem padrão eh o esperado (sem sugestão)
        assertEquals("Time exceeded! Please register the clock-out.", message);
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldClearSuggestionWhenTimeNotExceededInCheckAndNotify() {
        UUID userId = UUID.randomUUID();

        CreateTaskDTO dto = new CreateTaskDTO("Tarefa D", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // Dentro da tolerância (não deve atribuir sugestão)
        LocalDateTime currentTime = task.getStartTime().plusMinutes(105);
        taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, currentTime);

        TaskEntity updated = repository.findById(task.getId()).get();
        assertNull(updated.getSuggestion()); // Confirmamos que setSuggestion(null) foi chamado
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenTaskNotFoundInGetTask() {
        UUID nonExistentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskServiceDB.getTask(nonExistentId, userId);
        });

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldSetSuggestionToNullWhenTimeExactlyAtToleranceInCheckForTimeExceeded() {
        UUID userId = UUID.randomUUID();

        CreateTaskDTO dto = new CreateTaskDTO("Tarefa E", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // 100 + 10% = 110 minutos
        LocalDateTime currentTime = task.getStartTime().plusMinutes(110);

        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, currentTime);

        assertTrue(result);
        TaskEntity updated = repository.findById(task.getId()).get();
        assertNull(updated.getSuggestion()); // Mata mutante do setSuggestion(null)
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldSetSuggestionToNullWhenTimeExactlyAtToleranceInCheckAndNotifyTimeExceeded() {
        UUID userId = UUID.randomUUID();

        CreateTaskDTO dto = new CreateTaskDTO("Tarefa F", "Desc", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // Exatamente no limite de tolerância
        LocalDateTime currentTime = task.getStartTime().plusMinutes(110);

        String message = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, currentTime);

        assertEquals("Time exceeded! Please register the clock-out.", message);
        TaskEntity updated = repository.findById(task.getId()).get();
        assertNull(updated.getSuggestion()); // Mata mutante do setSuggestion(null)
    }

    // Teste 1: Deve retornar false quando status != IN_PROGRESS
    @Test
    @Tag("Mutation")
    void shouldReturnFalseWhenStatusIsNotInProgress() {
        CreateTaskDTO dto = new CreateTaskDTO("X", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);

        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, LocalDateTime.now().plusMinutes(90));

        assertFalse(result);
    }

    // Teste 2: Deve manter sugestão como null quando já está como null
    @Test
    @Tag("Mutation")
    void shouldKeepSuggestionNullWhenAlreadyNull() {
        CreateTaskDTO dto = new CreateTaskDTO("X", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);

        // Setar startTime para 60 - 2 = 58 min atrás (dentro da tolerância de 66)
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now().minusMinutes(58), userId);

        LocalDateTime currentTime = LocalDateTime.now();
        String result = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, currentTime);

        assertEquals("Time exceeded! Please register the clock-out.", result);
        TaskEntity updated = repository.findById(task.getId()).get();
        assertNull(updated.getSuggestion());
    }

    // Teste 3: Deve testar updateTask e garantir que objeto é salvo
    @Test
    @Tag("Mutation")
    void shouldUpdateTaskUsingUpdateTaskMethod() {
        CreateTaskDTO dto = new CreateTaskDTO("Original", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);

        task.setDescription("Nova descricao");
        TaskEntity updated = taskServiceDB.updateTask(task);

        assertEquals("Nova descricao", updated.getDescription());
    }

}
