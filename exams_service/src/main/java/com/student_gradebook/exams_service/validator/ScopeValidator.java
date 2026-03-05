package com.student_gradebook.exams_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class ScopeValidator implements ConstraintValidator<ValidScope, Map<Integer, Double>> {

    @Override
    public boolean isValid(Map<Integer, Double> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) return true;

        for (Integer percent : value.keySet()) {
            if (percent < 0 || percent > 100) return false;
        }

        for (Double grade : value.values()) {
            if (grade == null) return false;

            if (grade < 2.0 || grade > 5.0) {
                return false;
            }

            if ((grade * 2) % 1 != 0) {
                return false;
            }
        }
        return true;
    }
}
