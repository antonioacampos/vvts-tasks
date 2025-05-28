package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ViewSpecificTaskServiceTest {
    @Autowired
    private TaskServiceDB taskServiceDB;


    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C01/US005 - Should return a specific task by index")
    void shouldReturnSpecificTaskByIndex() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task task1 = taskService.createTask("Estudar", "Fazer revisão de VVTS", deadline, 20, userId1);
        Task task2 = taskService.createTask("Ler", "Capítulo 4", deadline.plusDays(1), 20, userId1);

        Task task = taskService.getTask(task2.getId(), userId1);

        assertThat(task.getTitle()).isEqualTo("Ler");
        assertThat(task.getDescription()).isEqualTo("Capítulo 4");
        assertThat(task.getDeadline()).isCloseTo(deadline.plusDays(1), within(1, ChronoUnit.SECONDS));
    }
}