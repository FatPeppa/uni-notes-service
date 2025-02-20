package org.skyhigh.notesservice.validation.exception;

import lombok.*;

import java.time.ZonedDateTime;

@Builder
public class InternalServerErrorException extends RequestException {
    private static final String code = "10009999";
    private static final String message = "Произошла системная ошибка. Пожалуйста, обратитесь к команде поддержки";
    private ZonedDateTime timestamp;
    private String debugMessage;

    public static String getStaticCode() {
        return code;
    }

    public static String getStaticMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
