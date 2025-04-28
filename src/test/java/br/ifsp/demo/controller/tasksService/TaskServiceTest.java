package br.ifsp.demo.controller.tasksService;

import jdk.jfr.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


class TaskServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should create new task with informed data")
    void ShouldCreateNewTaskWithInformedData() {
        //C02/US001
        TaskService taskService = new TaskService();

        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 28, 10, 40);

        Task task = taskService.createTask("Name", "Description", dateTime);

        assertThat(task.getName()).isEqualTo("Name");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getDeadline()).isEqualTo(dateTime);
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should not create task with blank title")
    void ShouldNotCreateTaskWithBlankTitle() {
        //C01/US001
        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        assertThatThrownBy(() -> taskService.createTask(" ", "Description", dateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create task with blank title");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should not create task with outdated deadline")
    void ShouldNotCreateTaskWithOutdatedDeadline() {
        //C03/US001
        TaskService taskService =  new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        assertThatThrownBy(() -> taskService.createTask("Name", "Description", dateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create task with outdated deadline");
    }
}