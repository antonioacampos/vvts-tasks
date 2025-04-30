package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MarkTaskAsCompletedServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C01/US006 - Should mark task as completed if it is in progress")
    void shouldMarkTaskAsCompletedIfInProgress() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Estudar", "Revisar VVTS", deadline);
        task.setStatus(TaskStatus.IN_PROGRESS);

        taskService.markAsCompleted(0);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }
    
}
