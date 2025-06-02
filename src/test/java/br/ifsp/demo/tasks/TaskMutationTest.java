package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskMutationTest {

    private final UUID userId = UUID.randomUUID();

    // --- Título e Deadline ---

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenSettingBlankTitle() {
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
    void shouldThrowExceptionWhenSettingOutdatedDeadline() {
        LocalDateTime outdatedDeadline = LocalDateTime.now().minusDays(1);
        Task task = new Task(UUID.randomUUID(), "Titulo", "Descricao", LocalDateTime.now().plusDays(1), userId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setDeadline(outdatedDeadline);
        });

        assertEquals("Cannot edit task with outdated deadline", exception.getMessage());
    }

    // --- Clock In ---

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInWithNullStartTime() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(2), userId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.clockIn(null);
        });

        assertEquals("Start time cannot be null", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInWithFutureStartTime() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(2), userId);
        LocalDateTime future = LocalDateTime.now().plusHours(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            task.clockIn(future);
        });

        assertEquals("Start time cannot be in the future", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockInIfTaskNotPending() {
        Task task = new Task(UUID.randomUUID(), "Titulo", "Desc", LocalDateTime.now().plusDays(1), userId);
        task.setStatus(TaskStatus.COMPLETED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            task.clockIn(LocalDateTime.now());
        });

        assertEquals("Only pending tasks can be started", exception.getMessage());
    }

    // --- Clock Out ---

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenClockOutWithoutBeingInProgress() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(1), userId);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            task.clockOut(LocalDateTime.now());
        });

        assertEquals("Task must be in progress to be clocked out", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnFinishTimeAfterClockOut() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(1), userId);
        LocalDateTime start = LocalDateTime.now().minusMinutes(30);
        LocalDateTime end = LocalDateTime.now();

        task.clockIn(start);
        task.clockOut(end);

        assertEquals(end, task.getFinishTime());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldCalculateCorrectTimeSpentAfterClockOut() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(1), userId);
        LocalDateTime start = LocalDateTime.now().minusMinutes(45);
        LocalDateTime end = LocalDateTime.now();

        task.clockIn(start);
        task.clockOut(end);

        assertEquals(45L, task.getTimeSpent());
    }

    // --- Status e User ID ---

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenMarkingAsCompletedIfNotInProgress() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Descrição", LocalDateTime.now().plusHours(1), userId);

        IllegalStateException exception = assertThrows(IllegalStateException.class, task::markAsCompleted);

        assertEquals("Task must be in progress to be marked as completed", exception.getMessage());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectEstimatedTime() {
        Task task = new Task(UUID.randomUUID(), "Tarefa", "Desc", LocalDateTime.now().plusHours(2), userId);
        task.setEstimatedTime(90L);

        assertEquals(90L, task.getEstimatedTime());
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldReturnCorrectUserId() {
        Task task = new Task(UUID.randomUUID(), "Título", "Desc", LocalDateTime.now().plusHours(1), userId);

        assertEquals(userId, task.getUserId());
    }

    // --- Teste auxiliar para cobertura total da classe Task.java ---

    @Test // Cobertura de Setters não utilizados diretamente
    @Tag("UnitTest")
    @Tag("Mutation")
    void shouldCoverAllSettersInTaskClass() {
        Task task = new Task(
                UUID.randomUUID(),
                "Inicial",
                "Descrição inicial",
                LocalDateTime.now(),
                UUID.randomUUID()
        );

        task.setTitle("Titulo");
        task.setDescription("Descricao");
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setStatus(TaskStatus.IN_PROGRESS); // necessário para permitir markAsCompleted()

        task.markAsCompleted(); // cobre linha 88

        assertAll("Setters",
                () -> assertEquals("Titulo", task.getTitle()),
                () -> assertEquals("Descricao", task.getDescription()),
                () -> assertEquals(TaskStatus.COMPLETED, task.getStatus())
        );
    }
}
