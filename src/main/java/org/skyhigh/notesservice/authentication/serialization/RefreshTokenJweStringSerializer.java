package org.skyhigh.notesservice.authentication.serialization;

import org.skyhigh.notesservice.authentication.model.Token;

import java.util.function.Function;

public interface RefreshTokenJweStringSerializer extends Function<Token, String> {
    @Override
    String apply(Token token);
}
