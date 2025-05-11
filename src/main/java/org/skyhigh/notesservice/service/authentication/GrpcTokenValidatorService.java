package org.skyhigh.notesservice.service.authentication;

import java.util.List;

public interface GrpcTokenValidatorService {
    void validateAccessToken(String accessToken);
    void validateRefreshToken(String refreshToken);
    void validateAccessTokenWithRole(String accessToken, List<String> authorities);
    void validateRefreshTokenWithRole(String refreshToken, List<String> authorities);
    long getUserIdFromAccessToken(String accessToken);
    String getUsernameFromAccessToken(String accessToken);
    String getUsernameFromRefreshToken(String refreshToken);
}
