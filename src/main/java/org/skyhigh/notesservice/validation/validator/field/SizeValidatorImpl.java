package org.skyhigh.notesservice.validation.validator.field;

import jakarta.validation.ValidationException;
import org.skyhigh.notesservice.validation.annotation.Size;
import org.skyhigh.notesservice.validation.exception.IncorrectFieldSizeException;

import java.lang.reflect.Field;

public class SizeValidatorImpl implements FieldValidator {
    private static final String messagePrefix = "Длина должна быть";

    @Override
    public void validate(Object entity, Field field) {
        try {
            if (String.class.isAssignableFrom(field.getType())) {
                String fieldValue = (String) field.get(entity);
                if (fieldValue != null) {
                    var min = Integer.parseInt(field.getAnnotation(Size.class).min());
                    var max = Integer.parseInt(field.getAnnotation(Size.class).max());
                    if (!checkStringSize(min, max, fieldValue))
                        throw new IncorrectFieldSizeException(
                                field.getName(),
                                generateMessage(min, max)
                        );
                }
            }
        } catch (IllegalAccessException e) {
            throw new ValidationException(e);
        }
    }

    private boolean checkStringSize(Integer min, Integer max, String str) {
        var strSize = str.length();
        if (min > 0 && max > 0) {
            return min <= strSize && strSize <= max;
        } else if (min > 0) {
            return min <= strSize;
        } else if (max > 0) {
            return max >= strSize;
        } return true;
    }

    private String generateMessage(Integer min, Integer max) {
        if (min > 0 && max > 0) {
            return String.format("%s от %s до %s", messagePrefix, min, max);
        } else if (min > 0) {
            return String.format("%s от %s", messagePrefix, min);
        } else if (max > 0) {
            return String.format("%s до %s", messagePrefix, max);
        } return null;
    }
}
