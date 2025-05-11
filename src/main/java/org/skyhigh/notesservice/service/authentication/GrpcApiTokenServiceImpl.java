package org.skyhigh.notesservice.service.authentication;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notesservice.validation.exception.GrpcResponseException;
import org.skyhigh.notesservice.validation.exception.InternalServerErrorException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class GrpcApiTokenServiceImpl implements GrpcApiTokenService {
    @GrpcClient("grpc-api-token-service")
    private ApiTokenServiceGrpc.ApiTokenServiceBlockingStub grpcApiTokenBlockingStub;

    @Override
    public AppClient getAppClient(String name) throws GrpcResponseException {
        log.debug(String.format("Getting app client request for name: {%s} is sending", name));
        try {
            var getAppClientResponse = grpcApiTokenBlockingStub
                    .getAppClient(GetAppClientRequest.newBuilder()
                            .setClientName(name)
                            .build());
            if (getAppClientResponse.getResponseResultCode() != ResponseResultCode.GET_APP_CLIENT_SUCCESS) {
                log.debug(String.format("Getting app client request for name: {%s} is got with error: {%s}",
                        name, getAppClientResponse.getResponseResultCode().name()));
                throw new GrpcResponseException(getAppClientResponse.getResponseResultCode());
            }
            log.debug(String.format("Getting app client request for name: {%s} is got successfully: {%s}",
                    name, getAppClientResponse));
            return getAppClientResponse.getAppClient();
        } catch (StatusRuntimeException e) {
            log.debug(String.format("Getting app client request for name: {%s} is got with status runtime error: {%s}",
                    name, e));
            throw InternalServerErrorException.builder()
                    .timestamp(ZonedDateTime.now())
                    .debugMessage(e.getMessage())
                    .build();
        }
    }
}
