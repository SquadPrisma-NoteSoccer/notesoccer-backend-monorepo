package com.squadprisma.notesoccer.league_service.domain.exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String code) { super(code); }
}
