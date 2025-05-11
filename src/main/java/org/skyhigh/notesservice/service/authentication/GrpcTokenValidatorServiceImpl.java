package org.skyhigh.notesservice.service.authentication;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.skyhigh.notesservice.authentication.exception.TokenAuthenticationException;
import org.skyhigh.notesservice.service.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.skyhigh.notesservice.authentication.filter.JwtAuthenticationFilter.BEARER_PREFIX;

@Service
@RequiredArgsConstructor
public class GrpcTokenValidatorServiceImpl implements GrpcTokenValidatorService {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void validateAccessToken(String accessToken) {
        if (StringUtils.isEmpty(accessToken) || !StringUtils.startsWith(accessToken, BEARER_PREFIX))
            throw new TokenAuthenticationException("Некорректный/отсутствует access токен");

        var jwt = accessToken.substring(BEARER_PREFIX.length());

        var token = jwtService.extractAccessOrRefreshToken(jwt);
        if (token == null)
            throw new TokenAuthenticationException("Некорректный/отсутствует access токен");

        var username = token.subject();

        if (StringUtils.isNotEmpty(username)) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);

            if (!jwtService.isAccessTokenValid(jwt, userDetails))
                throw new TokenAuthenticationException("Некорректный/отсутствует access токен");
        } else throw new TokenAuthenticationException("Некорректный/отсутствует access токен");
    }

    @Override
    public void validateRefreshToken(String refreshToken) {
        if (StringUtils.isEmpty(refreshToken) || !StringUtils.startsWith(refreshToken, BEARER_PREFIX))
            throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");

        var jwt = refreshToken.substring(BEARER_PREFIX.length());

        var token = jwtService.extractAccessOrRefreshToken(jwt);
        if (token == null)
            throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");

        var username = token.subject();

        if (StringUtils.isNotEmpty(username)) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);

            if (!jwtService.isRefreshTokenValid(jwt, userDetails))
                throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");
        } else throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");
    }

    @Override
    public void validateAccessTokenWithRole(String accessToken, List<String> authorities) {
        validateAccessToken(accessToken);
        var jwt = accessToken.substring(BEARER_PREFIX.length());
        validateTokenAuthorities(jwt, authorities);
    }

    @Override
    public void validateRefreshTokenWithRole(String refreshToken, List<String> authorities) {
        validateRefreshToken(refreshToken);
        var jwt = refreshToken.substring(BEARER_PREFIX.length());
        validateTokenAuthorities(jwt, authorities);
    }

    @Override
    public long getUserIdFromAccessToken(String accessToken) {
        if (accessToken.contains(BEARER_PREFIX))
            accessToken = accessToken.substring(BEARER_PREFIX.length());
        return userService.getByUsername(jwtService
                .extractAccessOrRefreshToken(accessToken).subject()).getId();
    }

    @Override
    public String getUsernameFromAccessToken(String accessToken) {
        if (accessToken.contains(BEARER_PREFIX))
            accessToken = accessToken.substring(BEARER_PREFIX.length());
        return jwtService.extractAccessOrRefreshToken(accessToken).subject();
    }

    @Override
    public String getUsernameFromRefreshToken(String refreshToken) {
        if (refreshToken.contains(BEARER_PREFIX))
            refreshToken = refreshToken.substring(BEARER_PREFIX.length());
        return jwtService.extractAccessOrRefreshToken(refreshToken).subject();
    }


    private void validateTokenAuthorities(String tokenString, List<String> authorities) {
        if (authorities == null || authorities.isEmpty()) return;
        var token = jwtService.extractAccessOrRefreshToken(tokenString);
        if (token.authorities() == null || token.authorities().isEmpty())
            throw new TokenAuthenticationException("Нет требуемых доступов в токене");
        if (!authorities.stream().allMatch(x -> token.authorities().stream().anyMatch(y -> y.equals(x))))
            throw new TokenAuthenticationException("Нет требуемых доступов в токене");
    }
}
