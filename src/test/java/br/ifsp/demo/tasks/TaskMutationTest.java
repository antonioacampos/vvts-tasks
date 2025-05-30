package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskMutationTest {

    @Autowired
    private TaskServiceDB taskServiceDB;

    private final UUID userId = UUID.randomUUID();

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenTitleIsBlank() {
        UUID userId = UUID.randomUUID();
        Task task = new Task(UUID.randomUUID(), "Valid title", "Description", LocalDateTime.now().plusDays(1), userId);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> task.setTitle(" ")
        );

        assertEquals("Cannot edit task with blank title", thrown.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenEditingWithOutdatedDeadline() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime validDeadline = LocalDateTime.now().plusDays(1);
        LocalDateTime outdatedDeadline = LocalDateTime.now().minusDays(1);

        Task task = new Task(id, "Titulo", "Descricao", validDeadline, userId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setDeadline(outdatedDeadline);
        });

        assertEquals("Cannot edit task with outdated deadline", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenMarkingCompletedIfNotInProgress() {
        Task task = new Task(UUID.randomUUID(), "Titulo", "Desc", LocalDateTime.now().plusDays(1), UUID.randomUUID());
        task.setStatus(TaskStatus.PENDING); // Não está em progresso

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            task.markAsCompleted();
        });

        assertEquals("Task must be in progress to be marked as completed", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInIfStatusNotPending() {
        Task task = new Task(UUID.randomUUID(), "Titulo", "Desc", LocalDateTime.now().plusDays(1), UUID.randomUUID());
        task.setStatus(TaskStatus.COMPLETED); // Status inválido

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            task.clockIn(LocalDateTime.now());
        });

        assertEquals("Only pending tasks can be started", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenMarkingAsCompletedWithInvalidStatus() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Descrição", LocalDateTime.now().plusHours(1), UUID.randomUUID());

        IllegalStateException exception = assertThrows(IllegalStateException.class, task::markAsCompleted);

        assertEquals("Task must be in progress to be marked as completed", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInWithNullStartTime() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(2), UUID.randomUUID());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.clockIn(null);
        });

        assertEquals("Start time cannot be null", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInWithFutureStartTime() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(2), UUID.randomUUID());

        LocalDateTime future = LocalDateTime.now().plusHours(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.clockIn(future);
        });

        assertEquals("Start time cannot be in the future", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockOutWithoutBeingInProgress() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(1), UUID.randomUUID());

        LocalDateTime finish = LocalDateTime.now().plusHours(1);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            task.clockOut(finish);
        });

        assertEquals("Task must be in progress to be clocked out", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectFinishTime() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(1), UUID.randomUUID());

        LocalDateTime start = LocalDateTime.now().minusMinutes(30);
        LocalDateTime end = LocalDateTime.now();
        task.clockIn(start);
        task.clockOut(end);

        assertEquals(end, task.getFinishTime());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectTimeSpent() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(1), UUID.randomUUID());

        LocalDateTime start = LocalDateTime.now().minusMinutes(45);
        LocalDateTime end = LocalDateTime.now();
        task.clockIn(start);
        task.clockOut(end);

        assertEquals(45L, task.getTimeSpent());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectEstimatedTime() {
        UUID id = UUID.randomUUID();
        Task task = new Task(id, "Tarefa", "Desc", LocalDateTime.now().plusHours(2), UUID.randomUUID());
        task.setEstimatedTime(90L);

        assertEquals(90L, task.getEstimatedTime());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectUserId() {
        UUID userId = UUID.randomUUID();
        Task task = new Task(UUID.randomUUID(), "Título", "Desc", LocalDateTime.now().plusHours(1), userId);

        assertEquals(userId, task.getUserId());
    }

}
