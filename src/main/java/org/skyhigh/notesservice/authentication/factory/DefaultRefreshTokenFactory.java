package org.skyhigh.notesservice.authentication.factory;

import lombok.Getter;
import lombok.Setter;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.UUID;

@Setter
@Getter
@Component
public class DefaultRefreshTokenFactory implements RefreshTokenFactory {
    private final Duration tokenTtl;

    public DefaultRefreshTokenFactory(
            @Qualifier("RefreshTokenTtl") Integer refreshTokenTtl
    ) {
        tokenTtl = Duration.ofHours(refreshTokenTtl);
    }

    @Override
    public Token apply(UserDetails userDetails) {
        var authorities = new LinkedList<String>();
        authorities.add("JWT_REFRESH");
        authorities.add("JWT_LOGOUT");
        userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> "GRANT_" + authority)
                .forEach(authorities::add);

        var now = Instant.now();
        return new Token(UUID.randomUUID(), userDetails.getUsername(), authorities, now, now.plus(this.tokenTtl));
    }
}
