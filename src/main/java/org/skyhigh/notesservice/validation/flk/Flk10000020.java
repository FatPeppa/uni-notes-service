package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000020 extends CommonFlk {
    @Getter private static final String code = "10000020";
    @Getter private static final String message = "У пользователя слишком много категорий";
    @Getter private static final String fieldName = null;

    public Flk10000020() {
        super(
                null,
                null,
                code,
                message
        );
    }

    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName) throws FlkException {}
}
