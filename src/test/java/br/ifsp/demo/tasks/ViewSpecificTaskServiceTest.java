package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ViewSpecificTaskServiceTest {
    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Description("C01/US005 - Should return a specific task by index")
    void shouldReturnSpecificTaskByIndex() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        taskService.createTask("Estudar", "Fazer revisão de VVTS", deadline, userId1);
        taskService.createTask("Ler", "Capítulo 4", deadline.plusDays(1), userId1);

        Task task = taskService.getTask(1, userId1);

        assertThat(task.getTitle()).isEqualTo("Ler");
        assertThat(task.getDescription()).isEqualTo("Capítulo 4");
        assertThat(task.getDeadline()).isEqualTo(deadline.plusDays(1));
    }
}