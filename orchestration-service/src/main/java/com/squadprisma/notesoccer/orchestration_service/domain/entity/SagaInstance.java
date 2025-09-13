package com.squadprisma.notesoccer.orchestration_service.domain.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.squadprisma.notesoccer.orchestration_service.domain.enumtype.SagaStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_instance", schema = "orchestration_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaInstance {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    private String currentStep;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private JsonNode data;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate(){
        this.createdAt = this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = OffsetDateTime.now();
    }

}
