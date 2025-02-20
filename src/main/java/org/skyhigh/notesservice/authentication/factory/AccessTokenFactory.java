package org.skyhigh.notesservice.authentication.factory;

import org.skyhigh.notesservice.authentication.model.Token;

import java.util.function.Function;

public interface AccessTokenFactory extends Function<Token, Token> {
    @Override
    Token apply(Token token);
}
