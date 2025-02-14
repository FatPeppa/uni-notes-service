package org.skyhigh.uninotesservice.validation.validator.field;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldValidator {
    void validate(Object entity, Field field);
}
