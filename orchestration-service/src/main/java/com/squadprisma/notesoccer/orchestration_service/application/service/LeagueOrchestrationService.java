package com.squadprisma.notesoccer.orchestration_service.application.service;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.application.port.out.LeagueServicePort;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Orquestra as chamadas para o league-service.
 * Faz o mapeamento de exceções Feign para ResponseStatusException, de forma
 * padronizada e consistente com o UserOrchestrationService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeagueOrchestrationService {

    private final LeagueServicePort port;

    public LigaResponse criarLiga(CreateLigaRequest req) {
        log.info("[ORC-LEAGUE] Iniciando criação de liga. nome={}", req.nome());
        try {
            var resp = port.criarLiga(req);
            log.info("[ORC-LEAGUE] Liga criada com sucesso no league-service. ligaId={}", resp.id());
            return resp;
        } catch (FeignException.Conflict ex) {
            log.warn("[ORC-LEAGUE] Conflito ao criar liga no league-service. detalhe={}", ex.contentUTF8());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.contentUTF8());
        } catch (RetryableException ex) {
            log.error("[ORC-LEAGUE] Timeout ao chamar league-service na criação de liga.", ex);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
        } catch (FeignException ex) {
            log.error("[ORC-LEAGUE] Erro genérico do league-service ao criar liga. status={}", ex.status(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: " + ex.status());
        }
    }

    public TimeResponse criarTime(UUID ligaId, String nome) {
        log.info("[ORC-LEAGUE] Iniciando criação de time. ligaId={}, nome={}", ligaId, nome);
        try {
            var req = new CreateTimeRequest(ligaId, nome);
            var resp = port.criarTime(req);
            log.info("[ORC-LEAGUE] Time criado com sucesso no league-service. ligaId={}, timeId={}",
                    ligaId, resp.id());
            return resp;
        } catch (FeignException.Conflict ex) {
            log.warn("[ORC-LEAGUE] Conflito ao criar time na liga. ligaId={}, nome={}, detalhe={}",
                    ligaId, nome, ex.contentUTF8());
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.contentUTF8());
        } catch (RetryableException ex) {
            log.error("[ORC-LEAGUE] Timeout ao chamar league-service na criação de time. ligaId={}", ligaId, ex);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
        } catch (FeignException ex) {
            log.error("[ORC-LEAGUE] Erro genérico do league-service ao criar time. ligaId={}, status={}", ligaId, ex.status(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: " + ex.status());
        }
    }

    public List<TimeResponse> criarTimesLote(UUID ligaId, List<CreateTimeLoteRequest> reqs) {
        try {
            log.info("[ORC-LEAGUE] Iniciando criação em lote de times. ligaId={}, quantidadeTimes={}",
                    ligaId, reqs.size());
            var resp = port.criarTimesLote(ligaId, reqs);
            log.info("[ORC-LEAGUE] Lote de times criado com sucesso. ligaId={}, quantidadeCriada={}",
                    ligaId, resp.size());
            return resp;
        } catch (FeignException.Conflict ex) {
            log.warn("[ORC-LEAGUE] Conflito ao criar lote de times. ligaId={}, detalhe={}",
                    ligaId, ex.contentUTF8());
            // aqui virão erros como TEAM_ALREADY_EXISTS, TEAM_LIMIT_REACHED, etc.
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.contentUTF8());
        } catch (RetryableException ex) {
            log.error("[ORC-LEAGUE] Timeout ao chamar league-service na criação em lote de times. ligaId={}",
                    ligaId, ex);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "league-service timeout");
        } catch (FeignException ex) {
            log.error("[ORC-LEAGUE] Erro genérico do league-service na criação em lote de times. ligaId={}, status={}",
                    ligaId, ex.status(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "league-service error: " + ex.status());
        }
    }

    public PageResponse<TimeResponse> listarTimes(UUID ligaId, int page, int size) {
        log.info("[ORC-LEAGUE] Listando times. ligaId={}, page={}, size={}", ligaId, page, size);
        var resp = port.listarTimes(ligaId, page, size);
        log.debug("[ORC-LEAGUE] Listagem de times concluída. ligaId={}, totalElements={}",
                ligaId, resp.totalElements());
        return resp;
    }

    public TimeCountResponse contarTimes(UUID ligaId) {
        log.info("[ORC-LEAGUE] Contando times da liga. ligaId={}", ligaId);
        var resp = port.contarTimes(ligaId);
        log.info("[ORC-LEAGUE] Contagem concluída. ligaId={}, totalTimes={}",
                ligaId, resp.count());
        return resp;
    }
}
