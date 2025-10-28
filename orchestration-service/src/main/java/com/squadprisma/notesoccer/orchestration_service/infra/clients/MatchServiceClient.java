package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.integrations.MatchServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "match-service",
        url = "${external.match-service.base-url}",
        configuration = MatchServiceFeignConfig.class,
        path = "/api/v1/partidas"
)
public interface MatchServiceClient {

    @PostMapping
    PartidaResponse criar(@RequestBody CreatePartidaRequest req);

    @GetMapping("/{id}")
    PartidaResponse buscarPorId(@PathVariable("id") UUID id);

    @GetMapping
    List<PartidaResponse> listar(@RequestParam(value = "ligaId", required = false) UUID ligaId,
                                @RequestParam(value = "timeId", required = false) UUID timeId);

    @PatchMapping("/{id}/status")
    PartidaResponse alterarStatus(@PathVariable("id") UUID id, @RequestParam("status") String status);
}
