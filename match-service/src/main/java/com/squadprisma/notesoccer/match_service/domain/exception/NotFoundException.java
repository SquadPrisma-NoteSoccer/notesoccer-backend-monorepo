package com.squadprisma.notesoccer.match_service.domain.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String code) { super(code);}
}
