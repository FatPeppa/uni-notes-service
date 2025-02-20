package org.skyhigh.notesservice.validation.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IncorrectFieldSizeException extends RequestException {
    private final String parameterName;
    private final String messageSuffix;

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
        return "Некорректная длина поля. " + messageSuffix;
    }
}
