package com.squadprisma.notesoccer.match_service.domain.exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String code) { super(code); }
}
