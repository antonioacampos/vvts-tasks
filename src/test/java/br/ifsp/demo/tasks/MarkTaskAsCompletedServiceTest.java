package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MarkTaskAsCompletedServiceTest {
    @Autowired
    private TaskServiceDB taskServiceDB;

    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C01/US006 - Should mark task as completed if it is in progress")
    void shouldMarkTaskAsCompletedIfInProgress() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Estudar", "Revisar VVTS", deadline, 120, userId1);
        taskService.clockIn(task.getId(), LocalDateTime.now(), userId1);

        taskService.markAsCompleted(task.getId(), userId1);
        Task taskUpdated = taskService.getTask(task.getId(), userId1);

        assertThat(taskUpdated.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C02/US006 - Should not allow completing task if status is not IN_PROGRESS")
    void shouldNotAllowCompletionIfTaskIsNotInProgress() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);
        Task task = taskService.createTask("Estudar", "Fazer resumo", deadline, 120, userId1);
        task.setStatus(TaskStatus.PENDING);

        assertThatThrownBy(() -> taskService.markAsCompleted(task.getId(), userId1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Task must be in progress to be marked as completed");
    }
}
