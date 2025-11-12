package com.squadprisma.notesoccer.orchestration_service.infra.feign;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignDownstreamConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new DownstreamErrorDecoder();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
