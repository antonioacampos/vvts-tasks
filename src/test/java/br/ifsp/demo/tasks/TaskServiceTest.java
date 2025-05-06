package br.ifsp.demo.tasks;
import jdk.jfr.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


class TaskServiceTest {
    UUID userId1 = UUID.randomUUID();
    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should create new task with informed data")
    void ShouldCreateNewTaskWithInformedData() {
        //C02/US001
        TaskService taskService = new TaskService();

        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 28, 10, 40);

        Task task = taskService.createTask("Name", "Description", dateTime, userId1);

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

        assertThatThrownBy(() -> taskService.createTask(" ", "Description", dateTime, userId1))
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

        assertThatThrownBy(() -> taskService.createTask("Name", "Description", dateTime, userId1))
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

        Task task = taskService.createTask("Name", "Description", dateTime, userId1);

        Task modTask = taskService.editTask(0, "Another name", "Another Description", dateTime.plusHours(5), userId1);

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

        Task task = taskService.createTask("Name", "Description", dateTime, userId1);

        assertThatThrownBy(() -> taskService.editTask(0, task.getTitle(), task.getDescription(), dateTime.minusHours(20), userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot edit task with outdated deadline");

        assertThatThrownBy(() -> taskService.editTask(0, " ", task.getDescription(), task.getDeadline(), userId1))
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

        Task task = taskService.createTask("Name", "Description", dateTime, userId1);

        assertThatThrownBy(() -> taskService.editTask(20, task.getTitle(), task.getDescription(), task.getDeadline(), userId1))
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

        taskService.createTask("Name 1", "Description", dateTime, userId1);
        taskService.createTask("Another name", "This Description", dateTime, userId1);

        String information = taskService.getAllInformation(userId1);

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

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should delete a task")
    void ShouldDeleteATask() {
        TaskService taskService = new TaskService();
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        taskService.createTask("Name 1", "Description", dateTime, userId1);
        taskService.deleteTask(0, userId1);

        String information = taskService.getAllInformation(userId1);

        assertThat(information).isEqualTo("");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should return the spent time when clock-out")
    void ShouldReturnSpentTimeWhenClockOut() {
        // C01/US009
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.of(2025, 11, 1, 12, 0);
        taskService.createTask("task-name", "task-desc", deadline, userId1);

        LocalDateTime startTime = LocalDateTime.now();
        taskService.clockIn(0, startTime, userId1);

        int timeToCompleteTask = 60;
        LocalDateTime finishTime = LocalDateTime.now().plusMinutes(timeToCompleteTask);

        taskService.clockOut(0, finishTime, userId1);

        assertEquals(timeToCompleteTask, taskService.getSpentTime(0, userId1));
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should return an error when clock-out without clock-in")
    void shouldReturnErrorWhenClockOutWithoutClockIn() {
        // C02/US009

        TaskService taskService = new TaskService();
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 1, 12, 0);
        taskService.createTask("task-name", "task-desc", deadline, userId1);
        LocalDateTime finishTime = LocalDateTime.of(2025, 12, 2, 2, 2);

        assertThrows(IllegalStateException.class, () -> taskService.clockOut(0, finishTime, userId1));
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should return exceeded time when clockout not registered")
    void ShouldNotifyTimeExceededWhenClockOutNotRegistered() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(0, startTime, userId1);

        boolean isTimeExceeded = taskService.checkForTimeExceeded(0, userId1);

        assertTrue(isTimeExceeded, "Task must be setted to 'exceeded time'.");
        assertEquals(TaskStatus.TIME_EXCEEDED, task.getStatus(), "Task status must be 'Time exceeded'.");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should notify the time exceeded when task is consulted")
    void ShouldNotifyTimeExceededWhenTaskIsConsulted() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(65);
        taskService.clockIn(0, startTime, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(0, userId1);

        assertEquals("Time exceeded! Please register the clock-out.", notification, "The system must notify that the time is exceeded.");
        assertEquals(TaskStatus.TIME_EXCEEDED, task.getStatus(), "The task status must be 'Time Exceeded'.");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should not suggest reevaluation if time exceeded is within tolerance")
    void ShouldNotSuggestReevaluationIfTimeExceededIsWithinTolerance() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(65);
        taskService.clockIn(0, startTime, userId1);
        String notification = taskService.checkAndNotifyTimeExceeded(0, userId1);

        assertEquals("Time exceeded! Please register the clock-out.", notification, "The system must notify that the time is exceeded without suggesting re-evaluation.");
        assertNull(task.getSuggestion(), "The system should not suggest re-evaluation when the time exceeded is within tolerance.");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should suggest reevaluation when time exceeded is above tolerance")
    void ShouldSuggestReevaluationWhenTimeExceededIsAboveTolerance() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(120);
        taskService.clockIn(0, startTime, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(0, userId1);
        assertEquals("Please re-evaluate or adjust the task.", notification, "The system should suggest task re-evaluation when time exceeds tolerance.");
        assertEquals(TaskStatus.TIME_EXCEEDED, task.getStatus(), "The task status must be 'Time Exceeded'.");
        assertNotNull(task.getSuggestion(), "The system should provide a suggestion for re-evaluation.");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should notify clock out forgotten when the time is exceeded")
    void ShouldNotifyClockOutForgottenWhenTimeExceeded() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(0, startTime, userId1);

        String notification = taskService.checkForClockOutForgotten(0, userId1);

        assertEquals("You forgot to clock out. Please register the clock-out.", notification, "The system should notify the user about the forgotten clock-out.");
    }

    @Test
    @Tag("@TDD")
    @Tag("@UnitTest")
    @Description("Should notify clock out not necessary when the task is completed")
    void ShouldNotifyClockOutNotNecessaryWhenTaskIsCompleted() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(0, startTime, userId1);

        task.setStatus(TaskStatus.COMPLETED);
        String notification = taskService.checkForClockOutForgottenInCompletedTask(0, userId1);

        assertEquals("Clock-out is no longer necessary as the task is already completed.", notification, "The system should notify that the clock-out is not necessary as the task is already completed.");
    }

    @Test
    @Tag("@UnitTest")
    @Description("Should notify that time is not exceeded if time is not exceeded")
    void shouldNotifyThatTimeIsNotExceededIfTimeIsNotExceeded(){
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        taskService.createTask("task-name", "task with no time exceeded", deadline, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(0, userId1);

        assertThat(notification).isEqualTo("Task is within the estimated time.");
    }

    @Test
    @Tag("@UnitTest")
    @Description("Should notify absence of check-in if get notification")
    void shouldNotifyAbsenceOfCheckInIfGetNotification() {
        TaskService taskService = new TaskService();

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        taskService.createTask("task-name", "task with no check-in", deadline, userId1);

        String notification = taskService.checkForClockOutForgotten(0, userId1);

        assertThat(notification).isEqualTo("Task is within the estimated time or clock-out is already registered.");
    }

}