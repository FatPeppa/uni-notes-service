package org.skyhigh.notesservice.validation.advice;

import lombok.extern.log4j.Log4j2;
import org.skyhigh.notesservice.authentication.exception.EmailNotFoundException;
import org.skyhigh.notesservice.authentication.exception.RefreshTokenDeactivatedException;
import org.skyhigh.notesservice.authentication.exception.TokenAuthenticationException;
import org.skyhigh.notesservice.model.dto.common.Error;
import org.skyhigh.notesservice.model.dto.common.Errors;
import org.skyhigh.notesservice.model.dto.common.SystemError;
import org.skyhigh.notesservice.validation.exception.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Log4j2
@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    private final boolean debugMode;

    public ExceptionAdvice(
            @Qualifier("DebugMode") boolean debugMode
    ) {
        this.debugMode = debugMode;
    }

    @ResponseBody
    @ExceptionHandler({NullParameterException.class, IncorrectFieldSizeException.class, NotEmailException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Error> requestParameterExceptionHandler(RequestException ex) {
        return ResponseEntity.ok(Error.builder()
                .code(ex.getCode())
                .attributeName(ex.getParameterName())
                .message(ex.getMessage())
                .build()
        );
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

    @ResponseBody
    @ExceptionHandler({FlkException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Errors multipleFlkExceptionHandler(FlkException ex) {
        return Errors.builder()
                .errors(List.of(Error.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .attributeName(ex.getParameterName())
                        .build()))
                .build();
    }


    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected SystemError systemExceptionHandler(RuntimeException e) {
        if (!(e instanceof RequestException) && !(e instanceof MultipleFlkException)) {
            return new SystemError.SystemErrorBuilder()
                    .setCode(InternalServerErrorException.getStaticCode())
                    .setMessage(InternalServerErrorException.getStaticMessage())
                    .setTimestamp(ZonedDateTime.now())
                    .setLocalizedMessage(debugMode ? e.getLocalizedMessage() : null)
                    .setExceptionMessage(debugMode ? e.getMessage() : null)
                    .setStackTrace(debugMode ? Arrays.stream(e.getStackTrace()).toString() : null)
                    .build();
        }
        throw e;
    }

    @ResponseBody
    @ExceptionHandler({RefreshTokenDeactivatedException.class, BadCredentialsException.class,
            TokenAuthenticationException.class, EmailNotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<?> systemExceptionHandler(Exception e) {
        if (e instanceof TokenAuthenticationException)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
