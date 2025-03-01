package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000004 extends CommonFlk{
    @Getter private static final String code = "10000004";
    @Getter private static final String message = "Заметка не существует или не принадлежит пользователю";
    @Getter private static final String fieldName = "noteId";

    public Flk10000004() {
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
