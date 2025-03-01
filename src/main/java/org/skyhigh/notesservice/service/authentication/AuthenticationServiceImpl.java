package org.skyhigh.notesservice.service.authentication;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.dto.authentication.AuthenticationResponse;
import org.skyhigh.notesservice.data.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.data.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.data.entity.Role;
import org.skyhigh.notesservice.data.entity.User;
import org.skyhigh.notesservice.service.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse signUp(SignUpRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .registerDate(ZonedDateTime.now())
                .lastLogonDate(ZonedDateTime.now())
                .blocked(false)
                .build();

        userService.create(user);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(jwtService.extractAccessTokenExpiration(accessToken).toString())
                .refreshToken(refreshToken)
                .refreshTokenExpiry(jwtService.extractRefreshTokenExpiration(refreshToken).toString())
                .build();
    }

    @Override
    public AuthenticationResponse signIn(SignInRequest request) {
        var username = request.getUsername();

        if (username == null || username.isEmpty()) {
            var user = userService.getUnblockedByEmail(request.getEmail());
            username = user.getUsername();
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                request.getPassword()
        ));

        var user = userService.userDetailsService()
                .loadUserByUsername(username);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        userService.updateLastLogonDateByUsername(username, ZonedDateTime.now());

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiry(jwtService.extractAccessTokenExpiration(accessToken).toString())
                .refreshToken(refreshToken)
                .refreshTokenExpiry(jwtService.extractRefreshTokenExpiration(refreshToken).toString())
                .build();
    }
}
