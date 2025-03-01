package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000010 extends CommonFlk {
    @Getter private static final String code = "10000010";
    @Getter private static final String message = "Основным файлом заметки может быть только TXT файл";
    @Getter private static final String fieldName = null;

    public Flk10000010() {
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
