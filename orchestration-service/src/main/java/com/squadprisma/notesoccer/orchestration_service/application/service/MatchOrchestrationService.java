package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.MatchServicePort;
import com.squadprisma.notesoccer.orchestration_service.domain.exception.ConflictException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchOrchestrationService {

    private final MatchServicePort matchPort;

    public PartidaResponse criar(CreatePartidaRequest req) {
        log.info("[ORC-MATCH] Iniciando criação de partida. casaTimeId={}, visitanteTimeId={}, startAt={}, endAt={}",
                req.casaTimeId(), req.visitanteTimeId(), req.startAt(), req.endAt());
        if (req.casaTimeId().equals(req.visitanteTimeId())) {
            log.warn("[ORC-MATCH] Validação falhou: times iguais na partida. timeId={}", req.casaTimeId());
            throw new IllegalArgumentException("Times devem ser distintos");
        }
        if (!req.startAt().isBefore(req.endAt())) {
            log.warn("[ORC-MATCH] Validação falhou: horário inválido. startAt={}, endAt={}",
                    req.startAt(), req.endAt());
            throw new ConflictException("Horário inválido");
        }
        try {
            var resp = matchPort.criar(req);
            log.info("[ORC-MATCH] Partida criada com sucesso no match-service. partidaId={}", resp.id());
            return resp;
        } catch (FeignException.Conflict e) {
            log.warn("[ORC-MATCH] Conflito de negócio ao criar partida no match-service. detalhe={}",
                    e.contentUTF8());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.contentUTF8(), e);
        } catch (RetryableException e) {
            log.error("[ORC-MATCH] Timeout ao chamar match-service na criação de partida.", e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            log.error("[ORC-MATCH] Erro genérico do match-service ao criar partida. status={}",
                    e.status(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "match-service error: " + e.status(), e);
        }
    }

    public List<PartidaResponse> calendario(UUID ligaId, OffsetDateTime from, OffsetDateTime to) {
        log.info("[ORC-MATCH] Consultando calendário de partidas. ligaId={}, from={}, to={}",
                ligaId, from, to);
        try {
            var resp = matchPort.calendario(ligaId, from, to);
            log.debug("[ORC-MATCH] Calendário retornado. ligaId={}, quantidadePartidas={}",
                    ligaId, resp.size());
            return resp;
        } catch (RetryableException e) {
            log.error("[ORC-MATCH] Timeout ao chamar match-service no calendário de partidas. ligaId={}",
                    ligaId, e);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "match-service timeout", e);
        } catch (FeignException e) {
            log.error("[ORC-MATCH] Erro genérico do match-service no calendário. ligaId={}, status={}",
                    ligaId, e.status(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "match-service error: " + e.status(), e);
        }
    }
}
