package org.skyhigh.notesservice.validation.flk.flk1000;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10000021 extends CommonFlk {
    @Getter private static final String code = "10000021";
    @Getter private static final String message = "Тег с указанным именем уже существует";
    @Getter private static final String fieldName = "name";

    public Flk10000021() {
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
