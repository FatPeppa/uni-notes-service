package org.skyhigh.notesservice.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.skyhigh.notesservice.authentication.exception.TokenAuthenticationException;
import org.skyhigh.notesservice.model.dto.authentication.AuthenticationResponse;
import org.skyhigh.notesservice.model.entity.User;
import org.skyhigh.notesservice.service.authentication.JwtService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {
    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/auth/v1/refresh", HttpMethod.POST.name());

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            var authHeader = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            var jwt = authHeader.substring(BEARER_PREFIX.length());

            var context = SecurityContextHolder.getContext();

            if (context != null) {
                if (context.getAuthentication() != null &&
                        context.getAuthentication() instanceof UsernamePasswordAuthenticationToken &&
                        context.getAuthentication().getPrincipal() instanceof User user &&
                        context.getAuthentication().getAuthorities()
                                .contains(new SimpleGrantedAuthority("JWT_REFRESH"))) {


                    if (jwtService.isRefreshTokenValid(jwt, user)) {
                        var accessToken = jwtService.generateAccessToken(user);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        this.objectMapper.writeValue(response.getWriter(),
                                new AuthenticationResponse(
                                        accessToken,
                                        jwtService.extractAccessTokenExpiration(accessToken).toString(),
                                        jwt,
                                        jwtService.extractRefreshTokenExpiration(jwt).toString()));
                        return;
                    } else {
                        throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");
                    }
                } else {
                    throw new TokenAuthenticationException("Некорректный/отсутствует refresh токен");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
