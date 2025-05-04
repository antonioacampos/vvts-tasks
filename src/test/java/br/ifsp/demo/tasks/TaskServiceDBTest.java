package br.ifsp.demo.tasks;

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
    void createTask_shouldPersistSuccessfully() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create(
            "Estudar VVTS", 
            "Fazer os testes da etapa 2", 
            LocalDateTime.now().plusDays(1), 
            3600, 
            userId
        );

        assertNotNull(task.getId());
        assertEquals("Estudar VVTS", task.getTitle());
        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void getAllByUser_shouldReturnOnlyUserTasks() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        service.create("Task U1", "Desc", LocalDateTime.now(), 1000, user1);
        service.create("Task U2", "Desc", LocalDateTime.now(), 1000, user2);

        List<TaskEntity> tasksU1 = service.getAllByUser(user1);
        assertEquals(1, tasksU1.size());
        assertEquals("Task U1", tasksU1.get(0).getTitle());
    }

    @Test
    void getByIdAndUser_shouldReturnCorrectTask() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create("Buscar café", "Desc", LocalDateTime.now(), 1000, userId);

        TaskEntity found = service.getByIdAndUser(task.getId(), userId);
        assertEquals(task.getId(), found.getId());
        assertEquals("Buscar café", found.getTitle());
    }

    @Test
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
    void shouldStartTaskIfPending() {
        UUID userId = UUID.randomUUID();
        // given
        TaskEntity created = service.create(
            "Estudar VVTS", 
            "Focar na Etapa 2", 
            LocalDateTime.now().plusDays(1), 
            60, 
            userId
        );

        // when
        TaskEntity updated = service.clockIn(created.getId(), userId);

        // then
        assertNotNull(updated.getStartTime());
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @Tag("TDD")
    void shouldNotStartTaskIfNotPending() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create("Tarefa", "desc", LocalDateTime.now().plusHours(1), 30, userId);

        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.now());
        repository.save(task);

        assertThrows(IllegalStateException.class, () -> {
            service.clockIn(task.getId(), userId);
        });
    }

    @Test
    @Tag("TDD")
    void shouldFinishTaskIfInProgress() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create("Ler livro", "Capítulo 3", LocalDateTime.now().plusHours(1), 30, userId);
        
        // Primeiro faz o clock-in
        service.clockIn(task.getId(), userId);

        // Faz o clock-out
        TaskEntity finished = service.clockOut(task.getId(), userId);

        assertNotNull(finished.getFinishTime());
        assertTrue(finished.getTimeSpent() >= 0);
        assertEquals(TaskStatus.COMPLETED, finished.getStatus());
    }

    @Test
    @Tag("TDD")
    void shouldNotFinishIfNotInProgress() {
        UUID userId = UUID.randomUUID();
        TaskEntity task = service.create("Escrever relatório", "Página 2", LocalDateTime.now().plusHours(2), 45, userId);

        // Tenta finalizar sem fazer clock-in
        assertThrows(IllegalStateException.class, () -> {
            service.clockOut(task.getId(), userId);
        });
    }

}
