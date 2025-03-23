package org.skyhigh.notesservice.service.authentication;

public interface GrpcTokenValidatorService {
    void validateAccessToken(String accessToken);
    void validateRefreshToken(String accessToken);
}
