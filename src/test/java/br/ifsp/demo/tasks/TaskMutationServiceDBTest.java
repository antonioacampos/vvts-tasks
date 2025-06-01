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
    void testCheckForTimeExceededScenarios() {
        // Cenário 1: dentro da tolerância
        TaskEntity task1 = createAndStartTask(userId, 100L, 105);
        boolean result1 = taskServiceDB.checkForTimeExceeded(task1.getId(), userId, LocalDateTime.now());
        assertTrue(result1);
        assertNull(task1.getSuggestion());

        // Cenário 2: status diferente de IN_PROGRESS
        CreateTaskDTO dto = new CreateTaskDTO("X", "Desc", LocalDateTime.now().plusHours(1), 60L, null);
        TaskEntity task2 = taskServiceDB.create(dto, userId);
        boolean result2 = taskServiceDB.checkForTimeExceeded(task2.getId(), userId, LocalDateTime.now().plusMinutes(90));
        assertFalse(result2);

        // Cenário 3: exatamente na tolerância
        TaskEntity task3 = createAndStartTask(userId, 100L, 110);
        boolean result3 = taskServiceDB.checkForTimeExceeded(task3.getId(), userId, LocalDateTime.now());
        assertTrue(result3);
        assertNull(repository.findById(task3.getId()).get().getSuggestion());
    }

    // ---------------------- checkAndNotifyTimeExceeded ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void testCheckAndNotifyTimeExceededScenarios() {
        // Cenário 1: dentro da tolerância
        TaskEntity task1 = createAndStartTask(userId, 100L, 105);
        String msg1 = taskServiceDB.checkAndNotifyTimeExceeded(task1.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, msg1);
        assertNull(repository.findById(task1.getId()).get().getSuggestion());

        // Cenário 2: sugestão já nula (tempo < estimado)
        TaskEntity task2 = createAndStartTask(userId, 60L, 58);
        String msg2 = taskServiceDB.checkAndNotifyTimeExceeded(task2.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, msg2);
        assertNull(repository.findById(task2.getId()).get().getSuggestion());

        // Cenário 3: exatamente no limite de tolerância
        TaskEntity task3 = createAndStartTask(userId, 100L, 110);
        String msg3 = taskServiceDB.checkAndNotifyTimeExceeded(task3.getId(), userId, LocalDateTime.now());
        assertEquals(TIME_EXCEEDED_MSG, msg3);
        assertNull(repository.findById(task3.getId()).get().getSuggestion());

        // Cenário 4: ultrapassando a tolerância
        TaskEntity task4 = createAndStartTask(userId, 100L, 115);
        String msg4 = taskServiceDB.checkAndNotifyTimeExceeded(task4.getId(), userId, LocalDateTime.now());
        assertEquals("Please re-evaluate or adjust the task.", msg4);
        assertNotNull(repository.findById(task4.getId()).get().getSuggestion());
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
