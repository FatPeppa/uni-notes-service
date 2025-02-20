package org.skyhigh.notesservice.validation.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotEmailException extends RequestException {
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
        return "Поле должно содержать адрес электронной почты";
    }
}
