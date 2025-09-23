package com.squadprisma.notesoccer.user_service.domain.exception;

public class EmailAlreadyInUseException extends RuntimeException{
    public EmailAlreadyInUseException(String email){
        super("Email já cadastrado: " + email);
    }
}
