package org.skyhigh.notesservice.validation.flk.flk1001;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10010000 extends CommonFlk {
    @Getter private static final String code = "10010000";
    @Getter private static final String message = "Пользователь по указанному ID не существует";
    @Getter private static final String fieldName = null;

    public Flk10010000() {
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
