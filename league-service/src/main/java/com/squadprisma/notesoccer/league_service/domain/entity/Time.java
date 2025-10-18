package com.squadprisma.notesoccer.league_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "times", schema = "league_dev")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private Liga liga;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
