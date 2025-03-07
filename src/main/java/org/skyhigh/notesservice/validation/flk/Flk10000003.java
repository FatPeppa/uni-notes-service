package org.skyhigh.notesservice.validation.flk;

import lombok.Getter;
import org.skyhigh.notesservice.model.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.validation.exception.FlkException;

/**
 * Проверка на заполнение имени пользователя или почты при аутентификации
 */
public class Flk10000003 extends CommonFlk  {
    @Getter private static final String code = "10000003";
    @Getter private static final String message = "Должны быть заполнены имя пользователя или его электронная почта";
    private static final String parameterName = "signInRequest";
    private static final String fieldName = "email";
    private static final Class<?> entityClass = SignInRequest.class;

    public Flk10000003() {
        super(
                parameterName,
                entityClass,
                code,
                message
        );
    }

    @Override
    public void validate(Object entity, Class<?> entityClass, String parameterName) throws FlkException {
        if (isSuitable(entityClass, parameterName)) {
            SignInRequest request = (SignInRequest) entity;
            if (request != null
                    && (request.getUsername() == null || request.getPassword().isEmpty())
                    && (request.getEmail() == null || request.getEmail().isEmpty()))
                throw FlkException.builder()
                        .flkCode(getFlkCode())
                        .flkMessage(getFlkMessage())
                        .flkParameterName(fieldName)
                        .build();
        }
    }
}
