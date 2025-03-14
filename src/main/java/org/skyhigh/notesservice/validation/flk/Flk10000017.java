package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000017 extends CommonFlk {
    @Getter private static final String code = "10000017";
    @Getter private static final String message = "Тег не существует или не принадлежит пользователю";
    @Getter private static final String fieldName = null;

    public Flk10000017() {
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
