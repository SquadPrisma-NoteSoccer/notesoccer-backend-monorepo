package com.squadprisma.notesoccer.match_service.domain.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String code) { super(code); }
}
