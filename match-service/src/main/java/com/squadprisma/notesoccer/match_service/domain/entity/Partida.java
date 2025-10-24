package com.squadprisma.notesoccer.match_service.domain.entity;

import com.squadprisma.notesoccer.match_service.domain.enums.PartidaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "partidas", schema = "match_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private UUID ligaId;

    @Column(nullable=false)
    private UUID casaTimeId;

    @Column(nullable=false)
    private UUID visitanteTimeId;

    @Column(nullable=false)
    private OffsetDateTime startAt;

    @Column(nullable=false)
    private OffsetDateTime endAt;

    private String local;

    private String notas;

    private UUID createdBy;

    @Column(nullable = false)
    private PartidaStatus status;

    @Column(nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable=false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    void onUpdate(){ this.updatedAt = OffsetDateTime.now();}
}
