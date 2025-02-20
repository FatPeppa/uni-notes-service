package org.skyhigh.notesservice.authentication.deserialization;

import org.skyhigh.notesservice.authentication.model.Token;

import java.util.function.Function;

public interface AccessTokenJwsStringDeserializer extends Function<String, Token> {
    @Override
    Token apply(String s);
}
