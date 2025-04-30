package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import jdk.jfr.Description;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FilterTasksByStatusServiceTest {

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("C02/US007 – Should return tasks filtered by valid status")
    void shouldReturnTasksFilteredByValidStatus() {
        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        Task t1 = taskService.createTask("Estudar", "Mat A", deadline);
        t1.setStatus(TaskStatus.IN_PROGRESS);

        Task t2 = taskService.createTask("Ler", "Cap 3", deadline.plusDays(1));
        t2.setStatus(TaskStatus.COMPLETED);

        List<Task> result = taskService.filterByStatus("IN_PROGRESS");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Estudar");
    }
}
