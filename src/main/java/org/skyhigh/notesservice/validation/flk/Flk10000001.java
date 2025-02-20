package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.data.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.validation.exception.FlkException;


public class Flk10000001 extends CommonFlk {
    @Getter private static final String code = "10000001";
    @Getter private static final String message = "Пользователь с указанным именем уже существует";
    @Getter private static final String fieldName = "username";

    public Flk10000001() {
        super(
                "SignUpRequest",
                SignUpRequest.class,
                "10000001",
                "Пользователь с указанным именем уже существует"
        );
    }

    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName) throws FlkException {}
}
