package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.model.dto.authentication.AuthenticationResponse;
import org.skyhigh.notesservice.model.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.model.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.service.authentication.AuthenticationService;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
@Tag(name = "Аутентификация и авторизация")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping(value = "/sign-up", consumes = "application/json", produces = "application/json")
    @ValidParams
    public ResponseEntity<AuthenticationResponse> signUp(
            @RequestBody SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(authenticationService.signUp(signUpRequest));
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping(value = "/sign-in", consumes = "application/json", produces = "application/json")
    @ValidParams
    public ResponseEntity<AuthenticationResponse> signIn(
            @RequestBody SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(authenticationService.signIn(signInRequest)) ;
    }
}
