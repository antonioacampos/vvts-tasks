package br.ifsp.demo.tasks;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findAllByUserId(UUID userId);
    Optional<TaskEntity> findByIdAndUserId(UUID id, UUID userId);
}
