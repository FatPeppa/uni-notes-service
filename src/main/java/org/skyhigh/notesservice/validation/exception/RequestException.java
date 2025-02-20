package org.skyhigh.notesservice.validation.exception;

public abstract class RequestException extends RuntimeException {
    public abstract String getCode();

    public abstract String getParameterName();

    @Override
    public abstract String getMessage();
}
