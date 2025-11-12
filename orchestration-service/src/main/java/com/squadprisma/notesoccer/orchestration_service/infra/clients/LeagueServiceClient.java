package com.squadprisma.notesoccer.orchestration_service.infra.clients;

import com.squadprisma.notesoccer.orchestration_service.api.dto.*;
import com.squadprisma.notesoccer.orchestration_service.infra.feign.FeignDownstreamConfig;
import com.squadprisma.notesoccer.orchestration_service.integrations.LeagueServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "league-service",
        url = "${external.league-service.base-url}",
        configuration = {LeagueServiceFeignConfig.class, FeignDownstreamConfig.class}
)
public interface LeagueServiceClient {
    @PostMapping("/api/v1/ligas")
    LigaResponse criarLiga(@RequestBody CreateLigaRequest body);

    @PostMapping("/api/v1/times")
    TimeResponse criarTime(@RequestBody CreateTimeRequest body);

    @GetMapping("/api/v1/times")
    PageResponse<TimeResponse> listarTimes(
            @RequestParam("ligaId") UUID ligaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    );

    @GetMapping("/api/v1/times/count")
    TimeCountResponse contarTimes(@RequestParam("ligaId") UUID ligaId);
}
