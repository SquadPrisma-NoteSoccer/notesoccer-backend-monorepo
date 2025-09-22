package com.squadprisma.notesoccer.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BrazilPhoneValidator.class)
public @interface BrazilPhone {
    String message() default "WhatsApp inválido. Informe até 14 dígitos (BR).";
    boolean optional() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
