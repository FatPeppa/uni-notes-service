package org.skyhigh.notesservice.validation.validator.field;

import jakarta.validation.ValidationException;
import org.skyhigh.notesservice.validation.exception.NotEmailException;

import java.lang.reflect.Field;

public class EmailValidatorImpl implements FieldValidator {
    private static final String regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @Override
    public void validate(Object entity, Field field) {
        try {
            if (String.class.isAssignableFrom(field.getType())) {
                String fieldValue = (String) field.get(entity);
                if (fieldValue != null) {
                    if (!fieldValue.matches(regexp))
                        throw new NotEmailException(field.getName());
                }
            }
        } catch (IllegalAccessException e) {
            throw new ValidationException(e);
        }
    }
}
