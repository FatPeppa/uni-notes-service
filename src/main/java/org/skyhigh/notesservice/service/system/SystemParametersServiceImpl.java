package org.skyhigh.notesservice.service.system;

import org.skyhigh.notesservice.data.dto.system.TokensTtl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SystemParametersServiceImpl implements SystemParametersService {
    private final Integer accessTokenTtl;
    private final Integer refreshTokenTtl;

    public SystemParametersServiceImpl(
            @Qualifier("AccessTokenTtl") Integer accessTokenTtl,
            @Qualifier("RefreshTokenTtl") Integer refreshTokenTtl
    ) {
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    @Override
    public TokensTtl getTokensTtl() {
        return TokensTtl.builder()
                .accessTokenTtl(accessTokenTtl)
                .refreshTokenTtl(refreshTokenTtl)
                .build();
    }
}
