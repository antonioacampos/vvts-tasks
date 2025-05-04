package br.ifsp.demo.tasks;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private long timeSpent;
    private long estimatedTime;

    private String suggestion;

    private UUID userId;
}
