package org.skyhigh.notesservice.validation.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NullParameterException extends RequestException {
    private final String parameterName;

    @Override
    public String getCode() {
        return "10000000";
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

    @Override
    public String getMessage() {
        return "Обязательное поле должно быть заполнено";
    }
}
