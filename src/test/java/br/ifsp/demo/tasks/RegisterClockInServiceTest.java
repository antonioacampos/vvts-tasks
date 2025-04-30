package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegisterClockInServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C02/US008 – Should register clock-in and update task status to IN_PROGRESS")
    void shouldRegisterClockInAndUpdateStatus() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Ler", "Livro A", deadline);

        taskService.clockIn(0);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getStartTime()).isNotNull();
    }
}
