package org.skyhigh.uninotesservice.validation.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class FlkException extends RequestException {
    private final String flkCode;
    private final String flkMessage;
    private final String flkParameterName;

    @Override
    public String getCode() {
        return flkCode;
    }

    @Override
    public String getParameterName() {
        return flkParameterName;
    }

    @Override
    public String getMessage() {
        return flkMessage;
    }
}
