package org.skyhigh.notesservice.validation.flk.flk1000;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10000016 extends CommonFlk {
    @Getter private static final String code = "10000016";
    @Getter private static final String message = "Фото не существует";
    @Getter private static final String fieldName = null;

    public Flk10000016() {
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
