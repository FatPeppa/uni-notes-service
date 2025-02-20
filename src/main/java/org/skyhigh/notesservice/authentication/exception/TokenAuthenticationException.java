package org.skyhigh.notesservice.authentication.exception;

import org.springframework.security.access.AccessDeniedException;

public class TokenAuthenticationException extends AccessDeniedException {
    public TokenAuthenticationException(String msg) {
        super(msg);
    }

    public TokenAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
