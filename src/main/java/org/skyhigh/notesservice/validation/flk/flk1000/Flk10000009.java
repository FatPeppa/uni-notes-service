package org.skyhigh.notesservice.validation.flk.flk1000;

import lombok.Getter;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.flk.CommonFlk;

public class Flk10000009 extends CommonFlk {
    @Getter private static final String code = "10000009";
    @Getter private static final String message = "Категория не существует или не принадлежит пользователю";
    @Getter private static final String fieldName = "categoryId";

    public Flk10000009() {
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
