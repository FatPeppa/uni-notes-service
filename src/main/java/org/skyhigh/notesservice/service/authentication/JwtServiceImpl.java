package org.skyhigh.notesservice.service.authentication;

import org.skyhigh.notesservice.authentication.model.Token;
import org.skyhigh.notesservice.authentication.deserialization.AccessTokenJwsStringDeserializer;
import org.skyhigh.notesservice.authentication.deserialization.RefreshTokenJweStringDeserializer;
import org.skyhigh.notesservice.authentication.factory.AccessTokenFactory;
import org.skyhigh.notesservice.authentication.factory.RefreshTokenFactory;
import org.skyhigh.notesservice.authentication.serialization.AccessTokenJwsStringSerializer;
import org.skyhigh.notesservice.authentication.serialization.RefreshTokenJweStringSerializer;
import org.skyhigh.notesservice.model.entity.DeactivatedToken;
import org.skyhigh.notesservice.repository.DeactivatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {
    private final AccessTokenFactory accessTokenFactory;
    private final RefreshTokenFactory refreshTokenFactory;
    private final AccessTokenJwsStringSerializer accessTokenJwsStringSerializer;
    private final RefreshTokenJweStringSerializer refreshTokenJweStringSerializer;
    private final AccessTokenJwsStringDeserializer accessTokenJwsStringDeserializer;
    private final RefreshTokenJweStringDeserializer refreshTokenJweStringDeserializer;

    private final DeactivatedTokenRepository deactivatedTokenRepository;

    @Autowired
    public JwtServiceImpl(
            AccessTokenFactory accessTokenFactory,
            RefreshTokenFactory refreshTokenFactory,
            AccessTokenJwsStringSerializer accessTokenJwsStringSerializer,
            RefreshTokenJweStringSerializer refreshTokenJweStringSerializer,
            AccessTokenJwsStringDeserializer accessTokenJwsStringDeserializer,
            RefreshTokenJweStringDeserializer refreshTokenJweStringDeserializer,
            DeactivatedTokenRepository deactivatedTokenRepository
    ) {
        this.accessTokenFactory = accessTokenFactory;
        this.refreshTokenFactory = refreshTokenFactory;
        this.accessTokenJwsStringSerializer = accessTokenJwsStringSerializer;
        this.refreshTokenJweStringSerializer = refreshTokenJweStringSerializer;
        this.accessTokenJwsStringDeserializer = accessTokenJwsStringDeserializer;
        this.refreshTokenJweStringDeserializer = refreshTokenJweStringDeserializer;
        this.deactivatedTokenRepository = deactivatedTokenRepository;
    }

    @Override
    public String extractUserNameFromAccessToken(String accessToken) throws ParseException {
        return accessTokenJwsStringDeserializer.apply(accessToken).subject();
    }

    @Override
    public String extractUserNameFromRefreshToken(String refreshToken) throws ParseException {
        return refreshTokenJweStringDeserializer.apply(refreshToken).subject();
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return accessTokenJwsStringSerializer.apply(
                accessTokenFactory.apply(new Token(
                    UUID.randomUUID(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                    null,
                    null)
                )
        );
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return refreshTokenJweStringSerializer.apply(refreshTokenFactory.apply(userDetails));
    }

    @Override
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        String userName;
        try {
            userName = extractUserNameFromAccessToken(token);
        } catch (Exception e) {
            return false;
        }
        return userName != null
                && userName.equals(userDetails.getUsername())
                && !isAccessTokenExpired(token)
                && !deactivatedTokenRepository.existsById(extractTokenIdFromAccessToken(token));
    }

    @Override
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        String userName;
        try {
            userName = extractUserNameFromRefreshToken(token);
        } catch (Exception e) {
            return false;
        }
        return userName != null
                && userName.equals(userDetails.getUsername())
                && !isRefreshTokenExpired(token)
                && !deactivatedTokenRepository.existsById(extractTokenIdFromRefreshToken(token));
    }

    @Override
    public Instant extractAccessTokenExpiration(String token) {
        return accessTokenJwsStringDeserializer.apply(token).expiresAt();
    }

    @Override
    public Instant extractRefreshTokenExpiration(String token) {
        return refreshTokenJweStringDeserializer.apply(token).expiresAt();
    }

    @Override
    public UUID extractTokenIdFromAccessToken(String token) {
        return accessTokenJwsStringDeserializer.apply(token).id();
    }

    @Override
    public UUID extractTokenIdFromRefreshToken(String token) {
        return refreshTokenJweStringDeserializer.apply(token).id();
    }

    @Override
    public void blockAccessToken(String token) {
        if (!deactivatedTokenRepository.existsById(extractTokenIdFromAccessToken(token)))
            deactivatedTokenRepository.save(DeactivatedToken.builder()
                    .id(extractTokenIdFromAccessToken(token))
                    .token(token)
                    .createdDate(LocalDateTime.now())
                    .build()
            );
    }

    @Override
    @CacheEvict(value = {"CurrentUserCache", "UserByEmailCache", "UserByUsernameCache"}, allEntries = true)
    public void blockRefreshToken(String token) {
        if (!deactivatedTokenRepository.existsById(extractTokenIdFromRefreshToken(token)))
            deactivatedTokenRepository.save(DeactivatedToken.builder()
                    .id(extractTokenIdFromRefreshToken(token))
                    .token(token)
                    .createdDate(LocalDateTime.now())
                    .build()
            );
    }

    @Override
    public boolean isAccessTokenActive(String token) {
        return deactivatedTokenRepository.findById(extractTokenIdFromAccessToken(token)).isEmpty();
    }
    @Override
    public boolean isRefreshTokenActive(String token) {
        return deactivatedTokenRepository.findById(extractTokenIdFromRefreshToken(token)).isEmpty();
    }

    @Override
    public Token extractAccessOrRefreshToken(String token) {
        try {
            var accessToken = this.accessTokenJwsStringDeserializer.apply(token);
            if (accessToken != null)
                return accessToken;

            return this.refreshTokenJweStringDeserializer.apply(token);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAccessTokenExpired(String token) {
        return extractAccessTokenExpiration(token).isBefore(Instant.now());
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractRefreshTokenExpiration(token).isBefore(Instant.now());
    }
}
