package org.skyhigh.notesservice.validation.flk.flk1000;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10000011 extends CommonFlk {
    @Getter private static final String code = "10000011";
    @Getter private static final String message = "Заметка с указанным именем уже существует";
    @Getter private static final String fieldName = "noteId";

    public Flk10000011() {
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
