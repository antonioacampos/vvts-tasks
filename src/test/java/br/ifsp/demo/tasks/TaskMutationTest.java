package br.ifsp.demo.tasks;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskMutationTest {

    @Autowired
    private TaskServiceDB taskServiceDB;

    private final UUID userId = UUID.randomUUID();

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    void shouldThrowExceptionWhenTitleIsBlank() {
        UUID userId = UUID.randomUUID();
        Task task = new Task(UUID.randomUUID(), "Valid title", "Description", LocalDateTime.now().plusDays(1), userId);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> task.setTitle(" ")
        );

        assertEquals("Cannot edit task with blank title", thrown.getMessage());
    }

}
