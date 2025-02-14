package org.skyhigh.uninotesservice.validation.advice;

import org.skyhigh.uninotesservice.data.dto.Error;
import org.skyhigh.uninotesservice.data.dto.Errors;
import org.skyhigh.uninotesservice.validation.exception.MultipleFlkException;
import org.skyhigh.uninotesservice.validation.exception.NullParameterException;
import org.skyhigh.uninotesservice.validation.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    @ResponseBody
    @ExceptionHandler({NullParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Error requestParameterExceptionHandler(RequestException ex) {
        return Error.builder()
                .code(ex.getCode())
                .attributeName(ex.getParameterName())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler({MultipleFlkException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Errors multipleFlkExceptionHandler(MultipleFlkException ex) {
        return Errors.builder()
                .errors(ex.getFlkExceptions().stream()
                        .map(x -> Error.builder()
                                .code(x.getCode())
                                .attributeName(x.getParameterName())
                                .message(x.getMessage())
                                .build())
                        .toList()
                )
                .build();
    }
}
