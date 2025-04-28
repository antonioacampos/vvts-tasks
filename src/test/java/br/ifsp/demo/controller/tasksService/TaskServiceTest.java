package br.ifsp.demo.controller.tasksService;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class TaskServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    void ShouldCreateNewTaskWithInformedData() {
        //C02/US001
        TaskService taskService = new TaskService();

        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 28, 10, 40);

        Task task = taskService.createTask("Name", "Description", dateTime);

        assertThat(task.getName()).isEqualTo("Name");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getDeadline()).isEqualTo(dateTime);
    }

}