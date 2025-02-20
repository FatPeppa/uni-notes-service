package org.skyhigh.notesservice.authentication.factory;

import lombok.Getter;
import lombok.Setter;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Setter
@Getter
@Component
public class DefaultAccessTokenFactory implements AccessTokenFactory {
    private final Duration tokenTtl;

    public DefaultAccessTokenFactory(
            @Qualifier("AccessTokenTtl") Integer accessTokenTtl
    ) {
        tokenTtl = Duration.ofMinutes(accessTokenTtl);
    }

    @Override
    public Token apply(Token token) {
        var now = Instant.now();
        return new Token(token.id(), token.subject(),
                token.authorities().stream()
                        .filter(authority -> authority.startsWith("GRANT_"))
                        .map(authority -> authority.substring(6))
                        .toList(), now, now.plus(this.tokenTtl));
    }
}
