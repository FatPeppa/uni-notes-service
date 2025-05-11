package org.skyhigh.notesservice.validation.flk.flk1000;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10000006 extends CommonFlk {
    @Getter private static final String code = "10000006";
    @Getter private static final String message = "Для заметки разрешено прикреплять только фото";
    @Getter private static final String fieldName = null;

    public Flk10000006() {
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
