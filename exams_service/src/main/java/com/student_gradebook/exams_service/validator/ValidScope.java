package com.student_gradebook.exams_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ScopeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScope {
    String message() default "Invalid grade value in scope";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
