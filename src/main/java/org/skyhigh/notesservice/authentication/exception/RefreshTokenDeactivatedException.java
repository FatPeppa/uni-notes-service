package org.skyhigh.notesservice.authentication.exception;

import org.springframework.security.core.AuthenticationException;

public class RefreshTokenDeactivatedException extends AuthenticationException {
    public RefreshTokenDeactivatedException() {
        super("refresh token deactivated");
    }
}
