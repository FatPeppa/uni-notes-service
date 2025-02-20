package org.skyhigh.notesservice.authentication.deserialization;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.extern.log4j.Log4j2;
import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.UUID;

@Log4j2
@Component
public class RefreshTokenJweStringDeserializerImpl implements RefreshTokenJweStringDeserializer {
    private final JWEDecrypter jweDecrypter;

    public RefreshTokenJweStringDeserializerImpl(
            @Qualifier("JWEDecrypter") JWEDecrypter jweDecrypter
    ) {
        this.jweDecrypter = jweDecrypter;
    }

    @Override
    public Token apply(String string) {
        try {
            var encryptedJWT = EncryptedJWT.parse(string);
            encryptedJWT.decrypt(this.jweDecrypter);
            var claimsSet = encryptedJWT.getJWTClaimsSet();
            return new Token(UUID.fromString(claimsSet.getJWTID()), claimsSet.getSubject(),
                    claimsSet.getStringListClaim("authorities"),
                    claimsSet.getIssueTime().toInstant(),
                    claimsSet.getExpirationTime().toInstant());
        } catch (ParseException | JOSEException exception) {
            log.error(exception.getMessage(), exception);
        }

        return null;
    }
}
