package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.CreatePartidaRequest;
import com.squadprisma.notesoccer.orchestration_service.api.dto.PartidaResponse;
import com.squadprisma.notesoccer.orchestration_service.infra.feign.FeignDownstreamConfig;
import com.squadprisma.notesoccer.orchestration_service.integrations.MatchServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "match-service",
        url = "${external.match-service.base-url}",
        configuration = {MatchServiceFeignConfig.class, FeignDownstreamConfig.class},
        path = "/api/v1/partidas"
)
public interface MatchServiceClient {

    @PostMapping
    PartidaResponse criar(@RequestBody CreatePartidaRequest req);

    @GetMapping("/calendario")
    List<PartidaResponse> calendario(
            @RequestParam(value = "ligaId") UUID ligaId,
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to);
}
