package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000014 extends CommonFlk {
    @Getter private static final String code = "10000014";
    @Getter private static final String message = "Заметка не содержит текстовый файл";
    @Getter private static final String fieldName = null;

    public Flk10000014() {
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
