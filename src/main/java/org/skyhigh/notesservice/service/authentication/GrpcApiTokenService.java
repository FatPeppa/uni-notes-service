package org.skyhigh.notesservice.service.authentication;

import org.shyhigh.grpc.notes.AppClient;
import org.skyhigh.notesservice.validation.exception.GrpcResponseException;

public interface GrpcApiTokenService {
    AppClient getAppClient(String name) throws GrpcResponseException;
}
