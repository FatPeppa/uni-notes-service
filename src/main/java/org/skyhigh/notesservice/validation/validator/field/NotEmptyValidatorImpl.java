package org.skyhigh.notesservice.validation.validator.field;

import jakarta.validation.ValidationException;
import org.skyhigh.notesservice.validation.exception.NullParameterException;

import java.lang.reflect.Field;
import java.util.Collection;

public class NotEmptyValidatorImpl implements FieldValidator {
    @Override
    public void validate(Object entity, Field field) {
        try {
            if (Collection.class.isAssignableFrom(field.getType())) {
                Collection<?> fieldValue = (Collection<?>) field.get(entity);
                if (fieldValue == null || fieldValue.isEmpty()) {
                    throw new NullParameterException(
                            field.getName()
                    );
                }
            } else if (String.class.isAssignableFrom(field.getType())) {
                String fieldValue = (String) field.get(entity);
                if (fieldValue == null || fieldValue.isEmpty()) {
                    throw new NullParameterException(field.getName());
                }
            } else {
                if (field.get(entity) == null) {
                    throw new NullParameterException(field.getName());
                }
            }
        } catch (IllegalAccessException e) {
            throw new ValidationException(e);
        }
    }
}
