package org.skyhigh.notesservice.authentication.serialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Log4j2
@Component
public class AccessTokenJwsStringSerializerImpl implements AccessTokenJwsStringSerializer {
    private final JWSSigner jwsSigner;

    private final JWSAlgorithm jwsAlgorithm;

    public AccessTokenJwsStringSerializerImpl(
            @Qualifier("JWSSigner") JWSSigner jwsSigner,
            @Qualifier("JWSAlgorithm") JWSAlgorithm jwsAlgorithm
    ) {
        this.jwsSigner = jwsSigner;
        this.jwsAlgorithm = jwsAlgorithm;
    }

    @Override
    public String apply(Token token) {
        var jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
                .keyID(token.id().toString())
                .build();
        var jwsClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.subject())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .claim("authorities", token.authorities())
                .build();
        var signedJWT = new SignedJWT(jwsHeader, jwsClaimsSet);

        try {
            signedJWT.sign(this.jwsSigner);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
