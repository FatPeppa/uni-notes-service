package org.skyhigh.notesservice.service.authentication;

import org.skyhigh.notesservice.data.dto.authentication.AuthenticationResponse;
import org.skyhigh.notesservice.data.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.data.dto.authentication.SignUpRequest;

public interface AuthenticationService {
    AuthenticationResponse signUp(SignUpRequest request);
    AuthenticationResponse signIn(SignInRequest request);
}
