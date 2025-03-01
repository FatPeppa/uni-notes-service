package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000012 extends CommonFlk {
    @Getter private static final String code = "10000012";
    @Getter private static final String message = "К заметке уже привязан текстовый файл";
    @Getter private static final String fieldName = null;

    public Flk10000012() {
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
