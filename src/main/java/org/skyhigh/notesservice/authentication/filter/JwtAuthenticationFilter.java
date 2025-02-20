package org.skyhigh.notesservice.authentication.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.skyhigh.notesservice.authentication.exception.TokenAuthenticationException;
import org.skyhigh.notesservice.service.authentication.JwtService;
import org.skyhigh.notesservice.service.UserService;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RequestMatcher requestLogoutMatcher = new AntPathRequestMatcher("/auth/logout", HttpMethod.POST.name());
    private final RequestMatcher requestRefreshMatcher = new AntPathRequestMatcher("/auth/refresh", HttpMethod.POST.name());

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authHeader.substring(BEARER_PREFIX.length());

        var token = jwtService.extractAccessOrRefreshToken(jwt);
        if (token == null)  {
            throw new TokenAuthenticationException("Некорректный/отсутствует access токен");
        }

        var username = token.subject();

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);

            var isTokenValid = false;

            isTokenValid = requestLogoutMatcher.matches(request) || requestRefreshMatcher.matches(request)
                    ? jwtService.isRefreshTokenValid(jwt, userDetails)
                    : jwtService.isAccessTokenValid(jwt, userDetails);

            if (isTokenValid) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        token.authorities().stream().map(SimpleGrantedAuthority::new).toList()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                throw new TokenAuthenticationException("Некорректный/отсутствует access токен");
            }
        }

        filterChain.doFilter(request, response);
    }
}
