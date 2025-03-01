package org.skyhigh.notesservice.authentication.exception;

import org.springframework.security.core.AuthenticationException;

public class UserIsBlockedException extends AuthenticationException  {
    public UserIsBlockedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserIsBlockedException(String msg) {
        super(msg);
    }
}
