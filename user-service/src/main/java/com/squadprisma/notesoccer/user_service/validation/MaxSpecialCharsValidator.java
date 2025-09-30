package com.squadprisma.notesoccer.user_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxSpecialCharsValidator implements ConstraintValidator<MaxSpecialChars, String> {

    private int max;

    @Override
    public void initialize(MaxSpecialChars constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        if (value == null) return true; // @NotBlank cuida do obrigatório
        int specials = 0;
        for (char c : value.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specials++;
            if (specials > max) return false;
        }
        return true;
    }
}
