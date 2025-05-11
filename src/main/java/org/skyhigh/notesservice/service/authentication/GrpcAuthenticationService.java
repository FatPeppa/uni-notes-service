package org.skyhigh.notesservice.service.authentication;

import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notesservice.model.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.model.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.FlkException;

import java.util.Base64;
import java.util.List;

import static org.skyhigh.notesservice.authentication.filter.JwtAuthenticationFilter.BEARER_PREFIX;

@GrpcService
@RequiredArgsConstructor
public class GrpcAuthenticationService extends GrpcAuthenticationServiceGrpc.GrpcAuthenticationServiceImplBase {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GrpcTokenValidatorService grpcTokenValidatorService;
    private final JwtService jwtService;

    @Override
    public void registerAdmin(AdminRegisterRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            var authenticationResponse = authenticationService.signUpAdmin(
                    SignUpRequest.builder()
                            .email(request.getEmail())
                            .username(request.getLogin())
                            .password(new String(Base64.getDecoder().decode(request.getPassword())))
                            .build()
            );
            responseObserver.onNext(AuthenticationResponse.newBuilder()
                    .setAccessToken(StringValue.of(authenticationResponse.getAccessToken()))
                    .setRefreshToken(StringValue.of(authenticationResponse.getRefreshToken()))
                    .setAccessTokenExpiry(StringValue.of(authenticationResponse.getAccessTokenExpiry()))
                    .setRefreshTokenExpiry(StringValue.of(authenticationResponse.getRefreshTokenExpiry()))
                    .setResponseResultCode(ResponseResultCode.ADMIN_REGISTER_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            var response = AuthenticationResponse.newBuilder();
            if (e instanceof FlkException)
                if (((FlkException) e).getCode().equals("10000001"))
                    response.setResponseResultCode(ResponseResultCode.ADMIN_REGISTER_FAILURE_USERNAME_NOT_FREE);
                else if (((FlkException) e).getCode().equals("10000002"))
                    response.setResponseResultCode(ResponseResultCode.ADMIN_REGISTER_FAILURE_EMAIL_NOT_FREE);
                else
                    response.setResponseResultCode(ResponseResultCode.UNEXPECTED_ERROR);
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void loginAdmin(AdminLoginRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            var authenticationResponse = authenticationService.signInAdmin(
                    SignInRequest.builder()
                            .email(request.hasEmail() ? request.getEmail() : null)
                            .username(request.hasUsername() ? request.getUsername() : null)
                            .password(new String(Base64.getDecoder().decode(request.getPassword())))
                            .build()
            );
            responseObserver.onNext(AuthenticationResponse.newBuilder()
                    .setAccessToken(StringValue.of(authenticationResponse.getAccessToken()))
                    .setRefreshToken(StringValue.of(authenticationResponse.getRefreshToken()))
                    .setAccessTokenExpiry(StringValue.of(authenticationResponse.getAccessTokenExpiry()))
                    .setRefreshTokenExpiry(StringValue.of(authenticationResponse.getRefreshTokenExpiry()))
                    .setResponseResultCode(ResponseResultCode.ADMIN_LOGIN_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            var response = AuthenticationResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.ADMIN_LOGIN_FAILURE);
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        try {
            grpcTokenValidatorService.validateRefreshTokenWithRole(
                    request.getRefreshToken(),
                    List.of("JWT_REFRESH")
            );
        } catch (Exception e) {
            responseObserver.onNext(AuthenticationResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UNAUTHORIZED_REQUEST)
                    .build());
            responseObserver.onCompleted();
            return;
        }
        try {
            var jwt = request.getRefreshToken().substring(BEARER_PREFIX.length());
            var token = jwtService.extractAccessOrRefreshToken(jwt);
            var user = userService.getByUsername(token.subject());
            var accessToken = jwtService.generateAccessToken(user);
            responseObserver.onNext(AuthenticationResponse.newBuilder()
                    .setAccessToken(StringValue.of(accessToken))
                    .setRefreshToken(StringValue.of(jwt))
                    .setAccessTokenExpiry(StringValue.of(jwtService
                            .extractAccessTokenExpiration(accessToken).toString()))
                    .setRefreshTokenExpiry(StringValue.of(jwtService
                            .extractRefreshTokenExpiration(jwt).toString()))
                    .setResponseResultCode(ResponseResultCode.ADMIN_REFRESH_ACCESS_TOKEN_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(AuthenticationResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.ADMIN_REFRESH_ACCESS_TOKEN_FAILURE)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void logoutAdmin(AdminLogoutRequest request, StreamObserver<AdminLogoutResponse> responseObserver) {
        try {
            grpcTokenValidatorService.validateRefreshTokenWithRole(
                    request.getRefreshToken(),
                    List.of("JWT_LOGOUT")
            );
        } catch (Exception e) {
            responseObserver.onNext(AdminLogoutResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UNAUTHORIZED_REQUEST)
                    .build());
            responseObserver.onCompleted();
            return;
        }
        try {
            var jwt = request.getRefreshToken().substring(BEARER_PREFIX.length());
            jwtService.blockRefreshToken(jwt);
            responseObserver.onNext(AdminLogoutResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.ADMIN_LOGOUT_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(AdminLogoutResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.ADMIN_LOGOUT_FAILURE)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void validateAccessToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        try {
            grpcTokenValidatorService.validateAccessToken(request.getAccessToken());

            var user = userService.getByUsername(
                    grpcTokenValidatorService.getUsernameFromAccessToken(request.getAccessToken())
            );
            responseObserver.onNext(ValidateTokenResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.VALIDATE_ACCESS_TOKEN_SUCCESS)
                    .setUserDetails(UserDetails.newBuilder()
                            .setUserId(user.getId())
                            .setUsername(user.getUsername())
                            .addAllGrantedAuthority(user.getAuthorities() != null ? user.getAuthorities().stream()
                                    .map(x -> StringValue.of(x.getAuthority())).toList() : List.of()))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(ValidateTokenResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.VALIDATE_ACCESS_TOKEN_FAILURE)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void validateRefreshToken(ValidateTokenRequest request, StreamObserver<ValidateTokenResponse> responseObserver) {
        try {
            grpcTokenValidatorService.validateRefreshToken(request.getRefreshToken());
            var user = userService.getByUsername(
                    grpcTokenValidatorService.getUsernameFromAccessToken(request.getRefreshToken())
            );
            responseObserver.onNext(ValidateTokenResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.VALIDATE_REFRESH_TOKEN_SUCCESS)
                    .setUserDetails(UserDetails.newBuilder()
                            .setUserId(user.getId())
                            .setUsername(user.getUsername())
                            .addAllGrantedAuthority(user.getAuthorities() != null ? user.getAuthorities().stream()
                                    .map(x -> StringValue.of(x.getAuthority())).toList() : List.of()))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(ValidateTokenResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.VALIDATE_REFRESH_TOKEN_FAILURE)
                    .build());
            responseObserver.onCompleted();
        }
    }
}
