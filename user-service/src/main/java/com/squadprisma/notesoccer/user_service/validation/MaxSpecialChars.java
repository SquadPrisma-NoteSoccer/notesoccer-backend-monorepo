package com.squadprisma.notesoccer.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MaxSpecialCharsValidator.class)
public @interface MaxSpecialChars {
    String message() default "Excesso de caracteres especiais.";
    int max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
