package org.skyhigh.notesservice.authentication.deserialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.log4j.Log4j2;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.UUID;

@Log4j2
@Component
public class AccessTokenJwsJwsStringDeserializerImpl implements AccessTokenJwsStringDeserializer {
    private final JWSVerifier jwsVerifier;

    public AccessTokenJwsJwsStringDeserializerImpl(
            @Qualifier("JWSVerifier") JWSVerifier jwsVerifier
    ) {
        this.jwsVerifier = jwsVerifier;
    }

    @Override
    public Token apply(String string) {
        try {
            var signedJWT = SignedJWT.parse(string);
            if (signedJWT.verify(this.jwsVerifier)) {
                var claimsSet = signedJWT.getJWTClaimsSet();
                return new Token(UUID.fromString(claimsSet.getJWTID()), claimsSet.getSubject(),
                        claimsSet.getStringListClaim("authorities"),
                        claimsSet.getIssueTime().toInstant(),
                        claimsSet.getExpirationTime().toInstant());
            }
        } catch (ParseException | JOSEException exception) {
            log.error(exception.getMessage(), exception);
        }
        return null;
    }
}
