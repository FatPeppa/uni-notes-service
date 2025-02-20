package org.skyhigh.notesservice.authentication.factory;

import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface RefreshTokenFactory extends Function<UserDetails, Token> {
    @Override
    Token apply(UserDetails authentication);
}
