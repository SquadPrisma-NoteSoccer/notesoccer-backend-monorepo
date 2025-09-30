package com.squadprisma.notesoccer.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BrazilPhoneValidator implements ConstraintValidator<BrazilPhone, String> {

    private boolean optional;

    @Override
    public void initialize(BrazilPhone annotation) {
        this.optional = annotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null || value.isBlank()) return optional;
        String digits = value.replaceAll("\\D", "");
        // Aceita 10–14 dígitos (ex.: AA + 8/9, 55 AA + 8/9). Rejeita >14.
        return digits.length() >= 10 && digits.length() <= 14;
    }
}
