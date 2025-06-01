package br.ifsp.demo.tasks;
import jdk.jfr.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskServiceTest {
    @Autowired
    private TaskServiceDB taskServiceDB;

    
    UUID userId1 = UUID.randomUUID();
    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should create new task with informed data")
    void ShouldCreateNewTaskWithInformedData() {
        //C02/US001
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime dateTime = LocalDateTime.now().plusDays(30);
        long estimatedTime = 120;

        Task task = taskService.createTask("Name", "Description", dateTime, estimatedTime, userId1);

        assertThat(task.getTitle()).isEqualTo("Name");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getDeadline()).isEqualTo(dateTime);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not create task with blank title")
    void ShouldNotCreateTaskWithBlankTitle() {
        //C01/US001
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        assertThatThrownBy(() -> taskService.createTask(" ", "Description", dateTime, 120, userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create task with blank title");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not create task with outdated deadline")
    void ShouldNotCreateTaskWithOutdatedDeadline() {
        //C03/US001
        TaskService taskService =  new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().minusHours(1);

        assertThatThrownBy(() -> taskService.createTask("Name", "Description", dateTime, 20, userId1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create task with outdated deadline");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should be able to edit tasks")
    void ShouldBeAbleToEditTasks() {
        //C02/US002

        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name", "Description", dateTime, 20, userId1);

        Task modTask = taskService.editTask(task.getId(), "Another name", "Another Description", dateTime.plusHours(5), userId1, LocalDateTime.now());

        assertThat(modTask.getTitle()).isEqualTo("Another name");
        assertThat(modTask.getDescription()).isEqualTo("Another Description");
        assertThat(modTask.getDeadline()).isEqualTo(dateTime.plusHours(5));

    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not be able to edit with the same check as creating")
    void ShouldNotBeAbleToEditWithTheSameCheckAsCreating() {
        //C01/US002
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name", "Description", dateTime, 20, userId1);

        assertThatThrownBy(() -> taskService.editTask(task.getId(), task.getTitle(), task.getDescription(), dateTime.minusHours(20), userId1, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot edit task with outdated deadline");

        assertThatThrownBy(() -> taskService.editTask(task.getId(), " ", task.getDescription(), task.getDeadline(), userId1, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot edit task with blank title");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should return information of all registered tasks")
    void ShouldReturnInformationOfAllRegisteredTasks() {

        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        taskService.createTask("Name 1", "Description", dateTime, 20, userId1);
        taskService.createTask("Another name", "This Description", dateTime, 20, userId1);

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
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should delete a task")
    void ShouldDeleteATask() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime dateTime = LocalDateTime.now().plusHours(5);

        Task task = taskService.createTask("Name 1", "Description", dateTime, 20, userId1);
        taskService.deleteTask(task.getId(), userId1);

        String information = taskService.getAllInformation(userId1);

        assertThat(information).isEqualTo("");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should return the spent time when clock-out")
    void ShouldReturnSpentTimeWhenClockOut() {
        // C01/US009
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.of(2025, 11, 1, 12, 0);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);

        LocalDateTime startTime = LocalDateTime.now();
        taskService.clockIn(task.getId(), startTime, userId1);

        int timeToCompleteTask = 60;
        LocalDateTime finishTime = LocalDateTime.now().plusMinutes(timeToCompleteTask);

        taskService.clockOut(task.getId(), finishTime, userId1);

        assertEquals(timeToCompleteTask, taskService.getSpentTime(task.getId(), userId1));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should return an error when clock-out without clock-in")
    void shouldReturnErrorWhenClockOutWithoutClockIn() {
        // C02/US009

        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.of(2025, 11, 1, 12, 0);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);
        LocalDateTime finishTime = LocalDateTime.of(2025, 12, 2, 2, 2);

        assertThrows(IllegalStateException.class, () -> taskService.clockOut(task.getId(), finishTime, userId1));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should return exceeded time when clockout not registered")
    void ShouldNotifyTimeExceededWhenClockOutNotRegistered() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(task.getId(), startTime, userId1);

        boolean isTimeExceeded = taskService.checkForTimeExceeded(task.getId(), userId1, LocalDateTime.now());
        Task taskUpdated = taskService.getTask(task.getId(), userId1);

        assertTrue(isTimeExceeded, "Task must be setted to 'exceeded time'.");
        assertEquals(TaskStatus.TIME_EXCEEDED, taskUpdated.getStatus(), "Task status must be 'Time exceeded'.");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify the time exceeded when task is consulted")
    void ShouldNotifyTimeExceededWhenTaskIsConsulted() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 60, userId1);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(65);
        taskService.clockIn(task.getId(), startTime, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(task.getId(), userId1, LocalDateTime.now());
        Task taskUpdated = taskService.getTask(task.getId(), userId1);

        assertEquals("Time exceeded! Please register the clock-out.", notification, "The system must notify that the time is exceeded.");
        assertEquals(TaskStatus.TIME_EXCEEDED, taskUpdated.getStatus(), "The task status must be 'Time Exceeded'.");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not suggest reevaluation if time exceeded is within tolerance")
    void ShouldNotSuggestReevaluationIfTimeExceededIsWithinTolerance() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 60, userId1);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(65);
        taskService.clockIn(task.getId(), startTime, userId1);
        String notification = taskService.checkAndNotifyTimeExceeded(task.getId(), userId1, LocalDateTime.now());
        Task taskUpdated = taskService.getTask(task.getId(), userId1);

        assertEquals("Time exceeded! Please register the clock-out.", notification, "The system must notify that the time is exceeded without suggesting re-evaluation.");
        assertNull(taskUpdated.getSuggestion(), "The system should not suggest re-evaluation when the time exceeded is within tolerance.");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should suggest reevaluation when time exceeded is above tolerance")
    void ShouldSuggestReevaluationWhenTimeExceededIsAboveTolerance() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(120);
        taskService.clockIn(task.getId(), startTime, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(task.getId(), userId1, LocalDateTime.now());
        Task taskUpdated = taskService.getTask(task.getId(), userId1);
        assertEquals("Please re-evaluate or adjust the task.", notification, "The system should suggest task re-evaluation when time exceeds tolerance.");
        assertEquals(TaskStatus.TIME_EXCEEDED, taskUpdated.getStatus(), "The task status must be 'Time Exceeded'.");
        assertNotNull(taskUpdated.getSuggestion(), "The system should provide a suggestion for re-evaluation.");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify clock out forgotten when the time is exceeded")
    void ShouldNotifyClockOutForgottenWhenTimeExceeded() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(task.getId(), startTime, userId1);

        String notification = taskService.checkForClockOutForgotten(task.getId(), userId1, LocalDateTime.now());

        assertEquals("You forgot to clock out. Please register the clock-out.", notification, "The system should notify the user about the forgotten clock-out.");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify clock out not necessary when the task is completed")
    void ShouldNotifyClockOutNotNecessaryWhenTaskIsCompleted() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task-desc", deadline, 20, userId1);

        long estimatedTime = 60;
        task.setEstimatedTime(estimatedTime);

        LocalDateTime startTime = LocalDateTime.now().minusMinutes(90);
        taskService.clockIn(task.getId(), startTime, userId1);
        taskService.clockOut(task.getId(), LocalDateTime.now(), userId1);

        String notification = taskService.checkForClockOutForgottenInCompletedTask(task.getId(), userId1);

        assertEquals("Clock-out is no longer necessary as the task is already completed.", notification, "The system should notify that the clock-out is not necessary as the task is already completed.");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify that time is not exceeded if time is not exceeded")
    void shouldNotifyThatTimeIsNotExceededIfTimeIsNotExceeded(){
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task with no time exceeded", deadline, 20, userId1);

        String notification = taskService.checkAndNotifyTimeExceeded(task.getId(), userId1, LocalDateTime.now());

        assertThat(notification).isEqualTo("Task is within the estimated time.");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify absence of check-in if get notification")
    void shouldNotifyAbsenceOfCheckInIfGetNotification() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task with no check-in", deadline, 20, userId1);

        String notification = taskService.checkForClockOutForgotten(task.getId(), userId1, LocalDateTime.now());

        assertThat(notification).isEqualTo("Task is within the estimated time or clock-out is already registered.");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should notify that task is not completed if asked of clock out forgotten")
    void shouldNotifyThatTaskIsNotCompletedIfAskedOfClockOutForgotten() {
        TaskService taskService = new TaskService(taskServiceDB);

        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        Task task = taskService.createTask("task-name", "task with no check-in", deadline, 20, userId1);

        String notification = taskService.checkForClockOutForgottenInCompletedTask(task.getId(), userId1);

        assertThat(notification).isEqualTo("Clock-out is not forgotten or the task is not completed.");
    }


    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not permit creation of task with any null value")
    void shouldNotPermitCreationOfTaskWithAnyNullValue() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        assertThatThrownBy(() -> taskService.createTask(null, "task with no title", deadline, 20, userId1))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.createTask("Title with no description", null, deadline, 20, userId1))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.createTask("Task with no deadline", "Task with no deadline", null, 20, userId1))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.createTask("Task with no user", "Task with no user", deadline, 20, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should not permit edition of task with any null value")
    void shouldNotPermitEditionOfTaskWithAnyNullValue() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        Task task = taskService.createTask("Task", "Task to be edited", deadline, 20, userId1);

        assertThatThrownBy(() -> taskService.editTask( task.getId(), null, "Task", deadline, userId1, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.editTask( task.getId(), "Task", null, deadline, userId1, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.editTask( task.getId(), "Task", "Task", null, userId1, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> taskService.editTask( task.getId(), "Task", "Task", deadline, null, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @Description("Should return false when time is not exceeded")
    void shouldReturnFalseWhenTimeIsNotExceeded() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        Task task = taskService.createTask("Task", "Task not exceeded", deadline, 20, userId1);

       assertThat(taskService.checkForTimeExceeded(task.getId(), userId1, LocalDateTime.now())).isFalse();
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    @Description("Should return completed task when mark as completed")
    void shouldReturnCompletedTaskWhenMarkAsCompleted() {
        TaskService taskService = new TaskService(taskServiceDB);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        Task task = taskService.createTask("Task to complete", "Task description", deadline, 60, userId1);
        taskService.clockIn(task.getId(), LocalDateTime.now().minusMinutes(30), userId1);

        Task completedTask = taskService.markAsCompleted(task.getId(), userId1);

        assertThat(completedTask).isNotNull();
        assertThat(completedTask.getTitle()).isEqualTo("Task to complete");
        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }
}