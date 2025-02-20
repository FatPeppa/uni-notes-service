package org.skyhigh.notesservice.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

@Configuration
public class TokenConfiguration {
    @Value("${spring.security.refresh-token-ttl}")
    private String refreshTokenTtl;

    @Value("${spring.security.access-token-ttl}")
    private String accessTokenTtl;

    @Value("${jwt.access-token-key}")
    private String accessTokenKey;

    @Value("${jwt.refresh-token-key}")
    private String refreshTokenKey;

    @Bean("RefreshTokenTtl")
    public Integer getRefreshTokenTtl() {return Integer.valueOf(refreshTokenTtl);}

    @Bean("AccessTokenTtl")
    public Integer getAccessTokenTtl() {return Integer.valueOf(accessTokenTtl);}

    private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
    private final JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    @Bean("JWSSigner")
    public JWSSigner jwsSigner() throws JOSEException, ParseException {
        return new MACSigner(OctetSequenceKey.parse(accessTokenKey));
    }

    @Bean("JWSAlgorithm")
    public JWSAlgorithm jwsAlgorithm() {
        return this.jwsAlgorithm;
    }

    @Bean("JWEEncrypter")
    public JWEEncrypter getJWEEncrypter() throws ParseException, KeyLengthException {
        return new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey));
    }

    @Bean("JWEAlgorithm")
    public JWEAlgorithm getJWEAlgorithm() {
        return this.jweAlgorithm;
    }

    @Bean("JWSVerifier")
    public JWSVerifier getJWSVerifier() throws ParseException, JOSEException {
        return new MACVerifier(OctetSequenceKey.parse(accessTokenKey));
    }

    @Bean("JWEDecrypter")
    public JWEDecrypter getJWEDecrypter() throws ParseException, KeyLengthException {
        return new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey));
    }
}
