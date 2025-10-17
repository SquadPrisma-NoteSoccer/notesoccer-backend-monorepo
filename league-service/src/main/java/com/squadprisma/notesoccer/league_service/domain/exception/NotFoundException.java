package com.squadprisma.notesoccer.league_service.domain.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String code) { super(code);}
}
