package com.squadprisma.notesoccer.orchestration_service.repository;

import com.squadprisma.notesoccer.orchestration_service.domain.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, UUID> {
}
