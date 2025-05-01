package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class RegisterClockInServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C02/US008 - Should register clock-in and update task status to IN_PROGRESS")
    void shouldRegisterClockInAndUpdateStatus() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);
        LocalDateTime startTime = LocalDateTime.now();
        Task task = taskService.createTask("Ler", "Livro A", deadline);

        taskService.clockIn(0, startTime);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getStartTime()).isNotNull();
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C01/US008 - Should throw error when trying to clock-in a completed task")
    void shouldThrowErrorWhenClockingInCompletedTask() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Estudar", "Matéria X", deadline);
        task.setStatus(TaskStatus.COMPLETED);

        LocalDateTime startTime = LocalDateTime.now();
        org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy(() -> taskService.clockIn(0, startTime))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending tasks can be started");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C03/US008 - Should not allow clock-in if task is already in progress")
    void shouldNotAllowClockInIfTaskIsAlreadyInProgress() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Projeto", "Clock-in duplo", deadline);
        task.setStatus(TaskStatus.IN_PROGRESS);

        LocalDateTime startTime = LocalDateTime.now();
        assertThatThrownBy(() -> taskService.clockIn(0, startTime))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only pending tasks can be started");
    }
}
