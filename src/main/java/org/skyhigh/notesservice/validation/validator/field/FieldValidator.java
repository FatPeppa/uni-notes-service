package org.skyhigh.notesservice.validation.validator.field;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldValidator {
    /**
     * Проверить поле сущности
     * @param entity - Проверяемая сущность
     * @param field - Проверяемое поле сущности
     */
    void validate(Object entity, Field field);
}
