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
public class RegisterClockInServiceTest {
    @Autowired
    private TaskServiceDB taskServiceDB;


    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C02/US008 - Should register clock-in and update task status to IN_PROGRESS")
    void shouldRegisterClockInAndUpdateStatus() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);
        LocalDateTime startTime = LocalDateTime.now();
        Task task = taskService.createTask("Ler", "Livro A", deadline, 120, userId1);

        taskService.clockIn(task.getId(), startTime, userId1);
        Task taskUpdated = taskService.getTask(task.getId(), userId1);
        assertThat(taskUpdated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(taskUpdated.getStartTime()).isNotNull();
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C01/US008 - Should throw error when trying to clock-in a completed task")
    void shouldThrowErrorWhenClockingInCompletedTask() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        taskService.clockIn(task.getId(), LocalDateTime.now(), userId1);
        taskService.clockOut(task.getId(), LocalDateTime.now().plusHours(1), userId1);

        LocalDateTime startTime = LocalDateTime.now();
        org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy(() -> taskService.clockIn(task.getId(), startTime, userId1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only pending tasks can be started");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C03/US008 - Should not allow clock-in if task is already in progress")
    void shouldNotAllowClockInIfTaskIsAlreadyInProgress() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Projeto", "Clock-in duplo", deadline, userId1);
        taskService.clockIn(task.getId(), LocalDateTime.now(), userId1);

        LocalDateTime startTime = LocalDateTime.now();
        assertThatThrownBy(() -> taskService.clockIn(task.getId(), startTime, userId1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only pending tasks can be started");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not allow clock-in in the future")
    void shouldNotAllowClockInInTheFuture(){
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Projeto", "Clock-in futuro", deadline, userId1);

        LocalDateTime clockin = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> taskService.clockIn(task.getId(), clockin, userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time cannot be in the future");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not allow clock-in null")
    void shouldNotAllowClockInNull(){
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task = taskService.createTask("Projeto", "Clock-in null", deadline, userId1);

        assertThatThrownBy(() -> taskService.clockIn(task.getId(), null, userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time cannot be null");
    }
}
