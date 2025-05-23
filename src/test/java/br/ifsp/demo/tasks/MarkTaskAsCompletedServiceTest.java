package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class MarkTaskAsCompletedServiceTest {
    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C01/US006 - Should mark task as completed if it is in progress")
    void shouldMarkTaskAsCompletedIfInProgress() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Estudar", "Revisar VVTS", deadline, userId1);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskService.markAsCompleted(0, userId1);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C02/US006 - Should not allow completing task if status is not IN_PROGRESS")
    void shouldNotAllowCompletionIfTaskIsNotInProgress() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Estudar", "Fazer resumo", deadline, userId1);
        task.setStatus(TaskStatus.PENDING);

        assertThatThrownBy(() -> taskService.markAsCompleted(0, userId1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Task must be in progress to be marked as completed");
    }
}
