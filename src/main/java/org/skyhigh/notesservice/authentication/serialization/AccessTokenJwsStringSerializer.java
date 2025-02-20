package org.skyhigh.notesservice.authentication.serialization;

import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public interface AccessTokenJwsStringSerializer extends Function<Token, String> {
    @Override
    String apply(Token token);
}
