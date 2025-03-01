package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000019 extends CommonFlk {
    @Getter private static final String code = "10000019";
    @Getter private static final String message = "Существуют привязанные к категории заметки";
    @Getter private static final String fieldName = null;

    public Flk10000019() {
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
