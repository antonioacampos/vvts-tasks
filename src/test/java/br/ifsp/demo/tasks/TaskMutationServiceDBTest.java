package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskMutationServiceDBTest {

    @Autowired
    private TaskServiceDB taskServiceDB;

    @Autowired
    private JpaTaskRepository repository;

    private static final UUID userId = UUID.randomUUID();
    private static final long DEFAULT_ESTIMATED_TIME = 100L;
    private static final String TIME_EXCEEDED_MSG = "Time exceeded! Please register the clock-out.";

    private TaskEntity createAndStartTask(UUID userId, long estimatedTime, long minutesAgo) {
        CreateTaskDTO dto = new CreateTaskDTO("Teste", "Desc", LocalDateTime.now().plusHours(1), estimatedTime, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        return taskServiceDB.clockIn(task.getId(), LocalDateTime.now().minusMinutes(minutesAgo), userId);
    }

    // ---------------------- checkForTimeExceeded ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTaskWithinTolerance_whenCheckForTimeExceeded_thenReturnTrueAndSuggestionNull() {
        TaskEntity task = createAndStartTask(userId, 100L, 105);
        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, LocalDateTime.now());
        assertTrue(result);
        assertNull(task.getSuggestion());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenStatusNotInProgress_whenCheckForTimeExceeded_thenReturnFalse() {
        CreateTaskDTO dto = new CreateTaskDTO("X", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, LocalDateTime.now().plusMinutes(90));
        assertFalse(result);
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTimeExactlyAtTolerance_whenCheckForTimeExceeded_thenSuggestionNull() {
        TaskEntity task = createAndStartTask(userId, 100L, 110);
        boolean result = taskServiceDB.checkForTimeExceeded(task.getId(), userId, LocalDateTime.now());
        assertTrue(result);
        assertNull(repository.findById(task.getId()).get().getSuggestion());
    }

    // ---------------------- checkAndNotifyTimeExceeded ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTaskWithinTolerance_whenCheckAndNotify_thenSuggestionNull() {
        TaskEntity task = createAndStartTask(userId, 100L, 105);
        String message = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, message);
        assertNull(repository.findById(task.getId()).get().getSuggestion());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenSuggestionNullAlready_whenCheckAndNotify_thenKeepSuggestionNull() {
        TaskEntity task = createAndStartTask(userId, 60L, 58);
        String result = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, result);
        assertNull(repository.findById(task.getId()).get().getSuggestion());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTimeExactlyAtTolerance_whenCheckAndNotify_thenSuggestionNull() {
        TaskEntity task = createAndStartTask(userId, 100L, 110);
        String result = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, result);
        assertNull(repository.findById(task.getId()).get().getSuggestion());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldSuggestReevaluationWhenExceededBeyondTolerance() {
        CreateTaskDTO dto = new CreateTaskDTO("Tarefa Mutante", "Divisão", LocalDateTime.now().plusHours(1), 100L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task = taskServiceDB.clockIn(task.getId(), LocalDateTime.now(), userId);

        // 100min + 10% = 110min → vamos usar 115min para ultrapassar a tolerância
        LocalDateTime currentTime = task.getStartTime().plusMinutes(115);

        String result = taskServiceDB.checkAndNotifyTimeExceeded(task.getId(), userId, currentTime);

        assertEquals("Please re-evaluate or adjust the task.", result);
    }

    // ---------------------- getTask ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenInvalidId_whenGetTask_thenThrowException() {
        UUID invalidId = UUID.randomUUID();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskServiceDB.getTask(invalidId, userId));
        assertEquals("Task not found", ex.getMessage());
    }

    // ---------------------- updateTask ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenUpdatedDescription_whenUpdateTask_thenPersistNewDescription() {
        CreateTaskDTO dto = new CreateTaskDTO("Original", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task = taskServiceDB.create(dto, userId);
        task.setDescription("Nova descricao");
        TaskEntity updated = taskServiceDB.updateTask(task);
        assertEquals("Nova descricao", updated.getDescription());
    }

    // ---------------------- filterByStatus ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTasksWithDifferentStatus_whenFilterByCompleted_thenReturnOnlyCompletedTasks() {
        TaskEntity pending = taskServiceDB.create(new CreateTaskDTO("Pending Task", "Desc", LocalDateTime.now().plusDays(1), 60L, null), userId);

        TaskEntity completed = taskServiceDB.create(new CreateTaskDTO("Completed Task", "Desc", LocalDateTime.now().plusDays(1), 60L, null), userId);
        taskServiceDB.clockIn(completed.getId(), LocalDateTime.now(), userId);
        taskServiceDB.clockOut(completed.getId(), LocalDateTime.now().plusMinutes(30), userId);

        List<TaskEntity> completedList = taskServiceDB.filterByStatus("COMPLETED", userId);

        assertEquals(1, completedList.size());
        assertEquals("Completed Task", completedList.get(0).getTitle());
    }
}
