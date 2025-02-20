package org.skyhigh.notesservice.validation.validator.flk;

import java.util.List;

@FunctionalInterface
public interface FlkValidator {
    void validate(Object entity, Class<?> entityClass, String parameterName,
                  List<String> currentMethodActiveFlk);
}
