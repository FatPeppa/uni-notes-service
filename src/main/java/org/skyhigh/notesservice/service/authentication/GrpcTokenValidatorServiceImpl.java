package org.skyhigh.notesservice.service.authentication;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.skyhigh.notesservice.authentication.exception.TokenAuthenticationException;
import org.skyhigh.notesservice.service.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);

            if (jwtService.isAccessTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        token.authorities().stream().map(SimpleGrantedAuthority::new).toList()
                );

                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                throw new TokenAuthenticationException("Некорректный/отсутствует access токен");
            }
        }
    }

    @Override
    public void validateRefreshToken(String accessToken) {

    }
}
