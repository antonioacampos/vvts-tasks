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

    @Column(nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime finishTime;

    @Column(nullable = true)
    private Long timeSpent;

    @Column(nullable = true)
    private Long estimatedTime;

    @Column(nullable = true)
    private String suggestion;

    private UUID userId;
}
