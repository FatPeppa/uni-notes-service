package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.data.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.validation.exception.FlkException;


/**
 * Проверка на наличие пользователя в системе по электронной почте
 */
public class Flk10000002 extends CommonFlk {
    @Getter private static final String code = "10000002";
    @Getter private static final String message = "Пользователь c указанной электронной почтой уже существует";
    @Getter private static final String fieldName = "email";

    public Flk10000002() {
        super(
                "SignUpRequest",
                SignUpRequest.class,
                code,
                message
        );
    }

    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName) throws FlkException {}
}
