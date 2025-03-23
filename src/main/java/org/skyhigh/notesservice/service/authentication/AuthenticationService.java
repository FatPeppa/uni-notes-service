package org.skyhigh.notesservice.service.authentication;

import org.skyhigh.notesservice.model.dto.authentication.AuthenticationResponse;
import org.skyhigh.notesservice.model.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.model.dto.authentication.SignUpRequest;

public interface AuthenticationService {
    AuthenticationResponse signUp(SignUpRequest request);
    AuthenticationResponse signIn(SignInRequest request);
    AuthenticationResponse signInAdmin(SignInRequest request);
    AuthenticationResponse signUpAdmin(SignUpRequest request);
}
