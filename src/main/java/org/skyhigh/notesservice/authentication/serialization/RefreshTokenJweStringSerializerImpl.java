package org.skyhigh.notesservice.authentication.serialization;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Setter
@Log4j2
@Component
public class RefreshTokenJweStringSerializerImpl implements RefreshTokenJweStringSerializer {
    private final JWEEncrypter jweEncrypter;

    private JWEAlgorithm jweAlgorithm;

    private EncryptionMethod encryptionMethod = EncryptionMethod.A128GCM;

    public RefreshTokenJweStringSerializerImpl(
            @Qualifier("JWEEncrypter") JWEEncrypter jweEncrypter,
            @Qualifier("JWEAlgorithm") JWEAlgorithm jweAlgorithm
    ) {
        this.jweEncrypter = jweEncrypter;
        this.jweAlgorithm = jweAlgorithm;
    }

    @Override
    public String apply(Token token) {
        var jwsHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
                .keyID(token.id().toString())
                .build();
        var jwsClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(token.id().toString())
                .subject(token.subject())
                .issueTime(Date.from(token.createdAt()))
                .expirationTime(Date.from(token.expiresAt()))
                .claim("authorities", token.authorities())
                .build();
        var encryptedJWT = new EncryptedJWT(jwsHeader, jwsClaimsSet);

        try {
            encryptedJWT.encrypt(this.jweEncrypter);

            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
