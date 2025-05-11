package org.skyhigh.notesservice.service.authentication;

import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notesservice.model.entity.Role;
import org.skyhigh.notesservice.repository.CategoryRepository;
import org.skyhigh.notesservice.repository.NoteRepository;
import org.skyhigh.notesservice.repository.TagRepository;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends GrpcUserServiceGrpc.GrpcUserServiceImplBase {
    private final UserService userService;
    private final GrpcTokenValidatorService grpcTokenValidatorService;

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void searchUsers(SearchUsersRequest request, StreamObserver<SearchUsersResponse> responseObserver) {
        try {
            grpcTokenValidatorService.validateAccessTokenWithRole(
                    request.getAccessToken(),
                    List.of(Role.ROLE_ADMIN.name())
            );
        } catch (Exception e) {
            responseObserver.onNext(SearchUsersResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UNAUTHORIZED_REQUEST)
                    .build());
            responseObserver.onCompleted();
            return;
        }

        try {
            var users = userService.searchUsers(request);
            List<User> grpcUsers;
            if (users != null && !users.isEmpty())
                grpcUsers = users.stream().map(x -> {
                    var userBuilder = User.newBuilder()
                            .setUserId(x.getId())
                            .setUsername(x.getUsername())
                            .setEmail(x.getEmail())
                            .setRole(x.getRole().name())
                            .setRegisterDate(x.getRegisterDate().toString())
                            .setLastLogonDate(x.getLastLogonDate().toString())
                            .setBlocked(x.isBlocked())
                            .setClientId(x.getClientId().toString());
                    if (request.getDetailType() == SearchUsersDetailType.WITH_OBJECTS_AMOUNT) {
                        var notesAmount = noteRepository.getUsersNotesAmount(x.getId());
                        var tagsAmount = tagRepository.getUsersTagsAmount(x.getId());
                        var categoriesAmount = categoryRepository.countUserCategories(x.getId());
                        userBuilder.setExtraUserDetails(ExtraUserDetails.newBuilder()
                                .setNotesAmount(notesAmount == null ? 0 : notesAmount)
                                .setTagsAmount(tagsAmount == null ? 0 : tagsAmount)
                                .setCategoriesAmount(categoriesAmount == null ? 0 : categoriesAmount)
                                .build());
                    }
                    return userBuilder.build();
                }).toList();
            else {
                responseObserver.onNext(SearchUsersResponse.newBuilder()
                        .setResponseResultCode(ResponseResultCode.SEARCH_USERS_FAILURE_DATA_NOT_EXIST)
                        .build());
                responseObserver.onCompleted();
                return;
            }
            responseObserver.onNext(SearchUsersResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.SEARCH_USERS_SUCCESS)
                    .addAllUsers(grpcUsers)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(SearchUsersResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UNEXPECTED_ERROR)
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void blockUser(BlockUserRequest request, StreamObserver<BlockUserResponse> responseObserver) {
        long userId = -1;
        try {
            grpcTokenValidatorService.validateAccessTokenWithRole(
                    request.getAccessToken(),
                    List.of(Role.ROLE_ADMIN.name())
            );
            userId = grpcTokenValidatorService.getUserIdFromAccessToken(request.getAccessToken());
        } catch (Exception e) {
            responseObserver.onNext(BlockUserResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UNAUTHORIZED_REQUEST)
                    .build());
            responseObserver.onCompleted();
            return;
        }

        var responseBuilder = BlockUserResponse.newBuilder();

        try {
            userService.blockUser(userId, request.getUserId());
            responseBuilder.setResponseResultCode(ResponseResultCode.BLOCK_USER_SUCCESS);
        } catch (MultipleFlkException e) {
            switch (e.getFlkExceptions().get(0).getCode()) {
                case "10010000":
                    responseBuilder.setResponseResultCode(ResponseResultCode
                            .BLOCK_USER_FAILURE_USER_NOT_EXIST);
                    break;
                case "10010001":
                    responseBuilder.setResponseResultCode(ResponseResultCode
                            .BLOCK_USER_FAILURE_USER_ALREADY_BLOCKED);
                    break;
                case "10010002":
                    responseBuilder.setResponseResultCode(ResponseResultCode
                            .BLOCK_USER_FAILURE_USER_CANNOT_BLOCK_SELF);
                    break;
                case "10010003":
                    responseBuilder.setResponseResultCode(ResponseResultCode
                            .BLOCK_USER_FAILURE_USER_CANNOT_BLOCK_ADMIN);
                    break;
            }
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserDetailsByUsername(GetUserDetailsByUsernameRequest request, StreamObserver<GetUserDetailsByUsernameResponse> responseObserver) {
        var user = userService.getByUsername(request.getUsername());
        if (user == null) {
            responseObserver.onNext(GetUserDetailsByUsernameResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.GET_USER_BY_USER_NAME_FAILURE)
                    .build());
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(GetUserDetailsByUsernameResponse.newBuilder()
                .setResponseResultCode(ResponseResultCode.GET_USER_BY_USER_NAME_SUCCESS)
                .setUserDetails(UserDetails.newBuilder()
                        .setUserId(user.getId())
                        .addAllGrantedAuthority(user.getAuthorities().stream()
                                .map(x -> StringValue.of(x.getAuthority())).toList())
                        .setUsername(user.getUsername())
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
