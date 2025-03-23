package org.skyhigh.notesservice.service.authentication;

import java.util.Base64;

import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notesservice.model.dto.authentication.SignInRequest;
import org.skyhigh.notesservice.model.dto.authentication.SignUpRequest;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.FlkException;

@GrpcService
@RequiredArgsConstructor
public class GrpcAuthenticationService extends GrpcAuthenticationServiceGrpc.GrpcAuthenticationServiceImplBase {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GrpcTokenValidatorService grpcTokenValidatorService;

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
    public void refreshToken(RefreshTokenRequest request, StreamObserver<AuthenticationResponse> responseObserver) {
        super.refreshToken(request, responseObserver);
    }

    @Override
    public void logoutAdmin(AdminLogoutRequest request, StreamObserver<AdminLogoutResponse> responseObserver) {
        super.logoutAdmin(request, responseObserver);
    }


    @Override
    public void validateAccessToken(ValidateAccessTokenRequest request, StreamObserver<ValidateAccessTokenResponse> responseObserver) {
        super.validateAccessToken(request, responseObserver);
    }
}
