package br.ifsp.demo.tasks;

import br.ifsp.demo.tasks.dtos.CreateTaskDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TaskServiceDBTest {

    @Autowired
    private TaskServiceDB service;

    @Autowired
    private JpaTaskRepository repository;

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void createTask_shouldPersistSuccessfully() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(
                new CreateTaskDTO("Estudar VVTS", "Fazer os testes da etapa 2", LocalDateTime.now().plusDays(1), 3600, null),
                userId
        );

        assertNotNull(task.getId());
        assertEquals("Estudar VVTS", task.getTitle());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getAllByUser_shouldReturnOnlyUserTasks() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        service.create(new CreateTaskDTO("Task U1", "Desc", LocalDateTime.now(), 1000, null), user1);
        service.create(new CreateTaskDTO("Task U2", "Desc", LocalDateTime.now(), 1000, null), user2);

        List<TaskEntity> tasksU1 = service.getAllByUser(user1);
        assertEquals(1, tasksU1.size());
        assertEquals("Task U1", tasksU1.get(0).getTitle());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getByIdAndUser_shouldReturnCorrectTask() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(new CreateTaskDTO("Buscar café", "Desc", LocalDateTime.now(), 1000, null), userId);

        TaskEntity found = service.getByIdAndUser(task.getId(), userId);
        assertEquals(task.getId(), found.getId());
        assertEquals("Buscar café", found.getTitle());
    }

    @Test
    @Tag("UnitTest")
    @Tag("Functional")
    void getByIdAndUser_shouldThrowWhenNotFound() {
        UUID userId = UUID.randomUUID();
        UUID fakeId = UUID.randomUUID();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getByIdAndUser(fakeId, userId);
        });

        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldStartTaskIfPending() {
        UUID userId = UUID.randomUUID();
        // given
        TaskEntity created = service.create(new CreateTaskDTO("Estudar VVTS", "Focar na etapa 2", LocalDateTime.now().plusDays(1), 60, null), userId);

        // when
        TaskEntity updated = service.clockIn(created.getId(), LocalDateTime.now(), userId);

        // then
        assertNotNull(updated.getStartTime());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotStartTaskIfNotPending() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(new CreateTaskDTO("Tarefa", "desc", LocalDateTime.now().plusHours(1), 30, null), userId);

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.now());
        repository.save(task);

        assertThrows(IllegalStateException.class, () -> {
            service.clockIn(task.getId(), LocalDateTime.now(), userId);
        });
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldFinishTaskIfInProgress() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(new CreateTaskDTO("Ler livro", "Capítulo 3", LocalDateTime.now().plusHours(1), 30, null), userId);
        
        // Primeiro faz o clock-in
        service.clockIn(task.getId(), LocalDateTime.now(), userId);

        // Faz o clock-out
        TaskEntity finished = service.clockOut(task.getId(), LocalDateTime.now().plusMinutes(10), userId);

        assertNotNull(finished.getFinishTime());
        assertTrue(finished.getTimeSpent() >= 0);
        assertEquals(TaskStatus.COMPLETED, finished.getStatus());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @Tag("Functional")
    void shouldNotFinishIfNotInProgress() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(new CreateTaskDTO("Escrever relatório", "Página 2", LocalDateTime.now().plusHours(2), 45, null), userId);

        // Tenta finalizar sem fazer clock-in
        assertThrows(IllegalStateException.class, () -> {
            service.clockOut(task.getId(), LocalDateTime.now(), userId);
        });
    }

}
