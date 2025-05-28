package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilterTasksByStatusServiceTest {
    @Autowired
    private TaskServiceDB taskServiceDB;


    UUID userId1 = UUID.randomUUID();

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C02/US007 - Should return tasks filtered by valid status")
    void shouldReturnTasksFilteredByValidStatus() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task t1 = taskService.createTask("Estudar", "Mat A", deadline, userId1);
        t1.setStatus(TaskStatus.IN_PROGRESS);

        Task t2 = taskService.createTask("Ler", "Cap 3", deadline.plusDays(1), userId1);
        t2.setStatus(TaskStatus.COMPLETED);

        List<Task> result = taskService.filterByStatus("IN_PROGRESS", userId1);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Estudar");
    }
    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("C01/US007 - Should throw error for invalid status")
    void shouldThrowErrorForInvalidStatus() {
        TaskService taskService = new TaskService();

        org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy(() -> taskService.filterByStatus("INVALID", userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid status: INVALID");
    }
}
