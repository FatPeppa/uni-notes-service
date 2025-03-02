package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;

public class Flk10000024 extends CommonFlk {
    @Getter private static final String code = "10000024";
    @Getter private static final String message = "Превышено максимальное количество тегов, по которым допустим одновременный поиск";
    @Getter private static final String fieldName = null;

    public Flk10000024() {
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
