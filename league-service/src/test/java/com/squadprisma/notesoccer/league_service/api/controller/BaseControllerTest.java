package com.squadprisma.notesoccer.league_service.api.controller;

import com.squadprisma.notesoccer.league_service.api.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandler.class)
public abstract class BaseControllerTest {
}
