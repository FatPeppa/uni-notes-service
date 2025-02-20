package org.skyhigh.notesservice.service.authentication;

import org.skyhigh.notesservice.authentication.model.Token;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.ParseException;
import java.time.Instant;
import java.util.UUID;

public interface JwtService {
    String extractUserNameFromAccessToken(String accessToken) throws ParseException;
    String extractUserNameFromRefreshToken(String refreshToken) throws ParseException;
    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    boolean isAccessTokenValid(String token, UserDetails userDetails);
    boolean isRefreshTokenValid(String token, UserDetails userDetails);
    Instant extractAccessTokenExpiration(String token);
    Instant extractRefreshTokenExpiration(String token);
    UUID extractTokenIdFromAccessToken(String token);
    UUID extractTokenIdFromRefreshToken(String token);
    void blockAccessToken(String token);
    void blockRefreshToken(String token);
    boolean isAccessTokenActive(String token);
    boolean isRefreshTokenActive(String token);
    Token extractAccessOrRefreshToken(String token);
}
