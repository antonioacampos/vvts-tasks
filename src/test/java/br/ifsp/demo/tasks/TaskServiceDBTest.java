package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskServiceDBTest {

    @Autowired
    private TaskServiceDB service;

    @Autowired
    private JpaTaskRepository repository;

    private static final UUID userId = UUID.randomUUID();
    private static final long DEFAULT_ESTIMATED_TIME = 100L;
    private static final String TIME_EXCEEDED_MSG = "Time exceeded! Please register the clock-out.";

    // ---------------------- Auxiliares ----------------------

    private TaskEntity createTask(String title) {
        return service.create(
                new CreateTaskDTO(title, "Desc", LocalDateTime.now().plusHours(1), DEFAULT_ESTIMATED_TIME, null),
                userId
        );
    }

    private TaskEntity createAndStartTask(UUID userId, long estimatedTime, long minutesAgo) {
        CreateTaskDTO dto = new CreateTaskDTO("Teste Mutacao", "Desc", LocalDateTime.now().plusHours(1), estimatedTime, null);
        TaskEntity task = service.create(dto, userId);
        return service.clockIn(task.getId(), LocalDateTime.now().minusMinutes(minutesAgo), userId);
    }

    // ---------------------- Testes funcionais ----------------------

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void createTask_shouldPersistSuccessfully() {
        TaskEntity task = createTask("Estudar VVTS");

        assertAll(
                () -> assertNotNull(task.getId()),
                () -> assertEquals("Estudar VVTS", task.getTitle()),
                () -> assertEquals(TaskStatus.PENDING, task.getStatus())
        );
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getAllByUser_shouldReturnOnlyUserTasks() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        service.create(new CreateTaskDTO("Task U1", "Desc", LocalDateTime.now(), 1000, null), user1);
        service.create(new CreateTaskDTO("Task U2", "Desc", LocalDateTime.now(), 1000, null), user2);

        List<TaskEntity> tasksU1 = service.getAllByUser(user1);

        assertAll(
                () -> assertEquals(1, tasksU1.size()),
                () -> assertEquals("Task U1", tasksU1.get(0).getTitle())
        );
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getByIdAndUser_shouldReturnCorrectTask() {
        TaskEntity task = createTask("Buscar café");
        TaskEntity found = service.getByIdAndUser(task.getId(), userId);

        assertAll(
                () -> assertEquals(task.getId(), found.getId()),
                () -> assertEquals("Buscar café", found.getTitle())
        );
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getByIdAndUser_shouldThrowWhenNotFound() {
        UUID fakeId = UUID.randomUUID();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getByIdAndUser(fakeId, userId);
        });

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldStartTaskIfPending() {
        TaskEntity task = createTask("Estudar VVTS");
        TaskEntity updated = service.clockIn(task.getId(), LocalDateTime.now(), userId);

        assertAll(
                () -> assertNotNull(updated.getStartTime()),
                () -> assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus())
        );
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotStartTaskIfNotPending() {
        TaskEntity task = createTask("Tarefa");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.now());
        repository.save(task);

        assertThrows(IllegalStateException.class, () -> {
            service.clockIn(task.getId(), LocalDateTime.now(), userId);
        });
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldFinishTaskIfInProgress() {
        TaskEntity task = createTask("Ler livro");
        service.clockIn(task.getId(), LocalDateTime.now(), userId);
        TaskEntity finished = service.clockOut(task.getId(), LocalDateTime.now().plusMinutes(10), userId);

        assertAll(
                () -> assertNotNull(finished.getFinishTime()),
                () -> assertTrue(finished.getTimeSpent() >= 0),
                () -> assertEquals(TaskStatus.COMPLETED, finished.getStatus())
        );
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotFinishIfNotInProgress() {
        TaskEntity task = createTask("Escrever relatório");

        assertThrows(IllegalStateException.class, () -> {
            service.clockOut(task.getId(), LocalDateTime.now(), userId);
        });
    }

    // ---------------------- Testes de mutação ----------------------

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void testCheckForTimeExceededScenarios() {
        // Cenário 1: dentro da tolerância
        TaskEntity task1 = createAndStartTask(userId, 100L, 105);
        assertAll("Cenário 1 - dentro da tolerância",
                () -> assertTrue(service.checkForTimeExceeded(task1.getId(), userId, LocalDateTime.now())),
                () -> assertNull(task1.getSuggestion())
        );

        // Cenário 2: status diferente de IN_PROGRESS
        TaskEntity task2 = service.create(new CreateTaskDTO("X", "Desc", LocalDateTime.now().plusHours(1), 60L, null), userId);
        assertAll("Cenário 2 - status diferente de IN_PROGRESS",
                () -> assertFalse(service.checkForTimeExceeded(task2.getId(), userId, LocalDateTime.now().plusMinutes(90)))
        );

        // Cenário 3: exatamente no limite
        TaskEntity task3 = createAndStartTask(userId, 100L, 110);
        assertAll("Cenário 3 - exatamente no limite",
                () -> assertTrue(service.checkForTimeExceeded(task3.getId(), userId, LocalDateTime.now())),
                () -> assertNull(repository.findById(task3.getId()).get().getSuggestion())
        );
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void testCheckAndNotifyTimeExceededScenarios() {
        // Cenário 1: dentro da tolerância
        TaskEntity task1 = createAndStartTask(userId, 100L, 105);
        assertAll("Cenário 1 - dentro da tolerância",
                () -> assertEquals(TIME_EXCEEDED_MSG, service.checkAndNotifyTimeExceeded(task1.getId(), userId, LocalDateTime.now())),
                () -> assertNull(repository.findById(task1.getId()).get().getSuggestion())
        );

        // Cenário 2: tempo abaixo do estimado
        TaskEntity task2 = createAndStartTask(userId, 60L, 58);
        assertAll("Cenário 2 - tempo abaixo do estimado",
                () -> assertEquals(TIME_EXCEEDED_MSG, service.checkAndNotifyTimeExceeded(task2.getId(), userId, LocalDateTime.now())),
                () -> assertNull(repository.findById(task2.getId()).get().getSuggestion())
        );

        // Cenário 3: exatamente no limite
        TaskEntity task3 = createAndStartTask(userId, 100L, 110);
        assertAll("Cenário 3 - exatamente no limite",
                () -> assertEquals(TIME_EXCEEDED_MSG, service.checkAndNotifyTimeExceeded(task3.getId(), userId, LocalDateTime.now())),
                () -> assertNull(repository.findById(task3.getId()).get().getSuggestion())
        );

        // Cenário 4: ultrapassando a tolerância
        TaskEntity task4 = createAndStartTask(userId, 100L, 115);
        assertAll("Cenário 4 - ultrapassando a tolerância",
                () -> assertEquals("Please re-evaluate or adjust the task.", service.checkAndNotifyTimeExceeded(task4.getId(), userId, LocalDateTime.now())),
                () -> assertNotNull(repository.findById(task4.getId()).get().getSuggestion())
        );
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenInvalidId_whenGetTask_thenThrowException() {
        UUID invalidId = UUID.randomUUID();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.getTask(invalidId, userId));
        assertEquals("Task not found", ex.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenUpdatedDescription_whenUpdateTask_thenPersistNewDescription() {
        TaskEntity task = createTask("Original");
        task.setDescription("Nova descricao");
        TaskEntity updated = service.updateTask(task);
        assertEquals("Nova descricao", updated.getDescription());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void givenTasksWithDifferentStatus_whenFilterByCompleted_thenReturnOnlyCompletedTasks() {
        service.create(new CreateTaskDTO("Pending Task", "Desc", LocalDateTime.now().plusDays(1), 60L, null), userId);

        TaskEntity completed = createTask("Completed Task");
        service.clockIn(completed.getId(), LocalDateTime.now(), userId);
        service.clockOut(completed.getId(), LocalDateTime.now().plusMinutes(30), userId);

        List<TaskEntity> completedList = service.filterByStatus("COMPLETED", userId);

        assertAll(
                () -> assertEquals(1, completedList.size()),
                () -> assertEquals("Completed Task", completedList.get(0).getTitle())
        );
    }
}
