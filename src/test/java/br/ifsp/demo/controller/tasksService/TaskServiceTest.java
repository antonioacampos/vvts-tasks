package br.ifsp.demo.controller.tasksService;

import jdk.jfr.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


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

        assertThat(task.getTitle()).isEqualTo("Name");
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
        LocalDateTime dateTime = LocalDateTime.now().minusHours(1);

        assertThatThrownBy(() -> taskService.createTask("Name", "Description", dateTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create task with outdated deadline");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should be able to edit tasks")
    void ShouldBeAbleToEditTasks() {
        //C02/US002

        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name", "Description", dateTime);

        Task modTask = taskService.editTask(0, "Another name", "Another Description", dateTime.plusHours(5));

        assertThat(modTask.getTitle()).isEqualTo("Another name");
        assertThat(modTask.getDescription()).isEqualTo("Another Description");
        assertThat(modTask.getDeadline()).isEqualTo(dateTime.plusHours(5));

    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should not be able to edit with the same check as creating")
    void ShouldNotBeAbleToEditWithTheSameCheckAsCreating() {
        //C01/US002
        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name", "Description", dateTime);

        assertThatThrownBy(() -> taskService.editTask(0, task.getTitle(), task.getDescription(), dateTime.minusHours(20)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot edit task with outdated deadline");

        assertThatThrownBy(() -> taskService.editTask(0, " ", task.getDescription(), task.getDeadline()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot edit task with blank title");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should give exception if index out of bounds")
    void ShouldGiveExceptionIfIndexOutOfBounds() {
        //C03/US002

        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name", "Description", dateTime);

        assertThatThrownBy(() -> taskService.editTask(20, task.getTitle(), task.getDescription(), task.getDeadline()))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("Index out of bounds");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should return information of all registered tasks")
    void ShouldReturnInformationOfAllRegisteredTasks() {

        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        taskService.createTask("Name 1", "Description", dateTime);
        taskService.createTask("Another name", "This Description", dateTime);

        String information = taskService.getAllInformation();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        String expected = String.format("{\n" +
                "  \"title\": \"Name 1\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"deadline\": \"%s\"\n" +
                "}\n" +
                "{\n" +
                "  \"title\": \"Another name\",\n" +
                "  \"description\": \"This Description\",\n" +
                "  \"deadline\": \"%s\"\n" +
                "}",
                dateTime.format(formatter),
                dateTime.format(formatter)
                );

        assertThat(information).isEqualTo(expected);
    }
}