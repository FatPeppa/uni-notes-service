package org.skyhigh.notesservice.repository;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notesservice.common.NoteMapper;
import org.skyhigh.notesservice.model.dto.search.*;
import org.skyhigh.notesservice.validation.exception.GrpcResponseException;
import org.skyhigh.notesservice.validation.exception.InternalServerErrorException;
import org.skyhigh.notesservice.validation.exception.RequestException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
@RequiredArgsConstructor
@Log4j2
public class NoteSearchRepositoryImpl implements NoteSearchRepository {
    private final NoteMapper noteMapper;

    @GrpcClient("note-data-modifying")
    private NoteDataModifyingServiceGrpc.NoteDataModifyingServiceBlockingStub noteDataModifyingStub;

    @GrpcClient("note-search")
    private NoteSearchServiceGrpc.NoteSearchServiceBlockingStub noteSearchStub;

    @Override
    @CacheEvict(value = {"NoteObjectCache"}, allEntries = true)
    public void createNote(NoteCreateRequestObject noteCreateRequestObject) throws RequestException, GrpcResponseException {
        log.debug(String.format("Create note request: {%s} is sending", noteCreateRequestObject));
        var noteCreateRequest = noteMapper.map(noteCreateRequestObject);
        try {
            DataModifyingResponse noteCreateResponse = noteDataModifyingStub
                    .createNote(noteCreateRequest);
            if (noteCreateResponse.getResponseResultCode() != ResponseResultCode.CREATE_SUCCESS) {
                log.debug(String.format("Create note request: {%s} is got with error: {%s}", noteCreateRequestObject, noteCreateResponse));
                throw new GrpcResponseException(noteCreateResponse
                        .getResponseResultCode());
            }
            log.debug(String.format("Create note request: {%s} is got successfully: {%s}", noteCreateRequestObject, noteCreateResponse));
        } catch (StatusRuntimeException e) {
            log.debug(String.format("Create note request: {%s} is got with status runtime error: {%s}", noteCreateRequestObject, e));
            throw InternalServerErrorException.builder()
                    .timestamp(ZonedDateTime.now())
                    .debugMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    @CacheEvict(value = {"NoteObjectCache"}, allEntries = true)
    public void updateNote(NoteUpdateRequestObject noteUpdateRequestObject) throws RequestException, GrpcResponseException {
        log.debug(String.format("Update note request: {%s} is sending", noteUpdateRequestObject));
        var noteUpdateRequest = noteMapper.map(noteUpdateRequestObject);
        try {
            DataModifyingResponse noteUpdateResponse = noteDataModifyingStub
                    .updateNote(noteUpdateRequest);
            if (noteUpdateResponse.getResponseResultCode() != ResponseResultCode.UPDATE_SUCCESS) {
                log.debug(String.format("Update note request: {%s} is got with error: {%s}", noteUpdateRequestObject, noteUpdateResponse));
                throw new GrpcResponseException(noteUpdateResponse
                        .getResponseResultCode());
            }
            log.debug(String.format("Update note request: {%s} is got successfully: {%s}", noteUpdateRequestObject, noteUpdateResponse));
        } catch (StatusRuntimeException e) {
            log.debug(String.format("Update note request: {%s} is got with status runtime error: {%s}", noteUpdateRequestObject, e));
            throw InternalServerErrorException.builder()
                    .timestamp(ZonedDateTime.now())
                    .debugMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    @CacheEvict(value = {"NoteObjectCache"}, allEntries = true)
    public void deleteNote(NoteDeleteRequestObject noteDeleteRequestObject) throws RequestException, GrpcResponseException {
        log.debug(String.format("Delete note request: {%s} is sending", noteDeleteRequestObject));
        var noteDeleteRequest = noteMapper.map(noteDeleteRequestObject);
        try {
            DataModifyingResponse noteDeleteResponse = noteDataModifyingStub
                    .deleteNote(noteDeleteRequest);
            if (noteDeleteResponse.getResponseResultCode() != ResponseResultCode.DELETE_SUCCESS) {
                log.debug(String.format("Delete note request: {%s} is got with error: {%s}", noteDeleteRequestObject, noteDeleteResponse));
                throw new GrpcResponseException(noteDeleteResponse
                        .getResponseResultCode());
            }
            log.debug(String.format("Delete note request: {%s} is got successfully: {%s}", noteDeleteRequestObject, noteDeleteResponse));
        } catch (StatusRuntimeException e) {
            log.debug(String.format("Delete note request: {%s} is got with status runtime error: {%s}", noteDeleteRequestObject, e));
            throw InternalServerErrorException.builder()
                    .timestamp(ZonedDateTime.now())
                    .debugMessage(e.getMessage())
                    .build();
        }
    }

    @Override
    @Cacheable(value = "NoteObjectCache",
            key="{#userId + #query}",
            unless = "#result == null"
    )
    public NoteSearchResponseObject searchNotes(
            NoteSearchRequestObject noteSearchRequestObject,
            Long userId,
            String query
    ) throws RequestException, GrpcResponseException {
        log.debug(String.format("Search notes request: {%s}  is sending", noteSearchRequestObject));
        var noteSearchRequest = noteMapper.map(noteSearchRequestObject);
        try {
            NoteSearchResponse noteSearchResponse = noteSearchStub
                    .searchNotes(noteSearchRequest);
            if (noteSearchResponse.getResponseResultCode() != ResponseResultCode.SEARCH_SUCCESS) {
                log.debug(String.format("Search note request: {%s} is got with error: {%s}", noteSearchRequestObject, noteSearchResponse));
                throw new GrpcResponseException(noteSearchResponse
                        .getResponseResultCode());
            }
            log.debug(String.format("Delete note request: {%s} is got successfully: {%s}", noteSearchRequestObject, noteSearchResponse));
            return noteMapper.map(noteSearchResponse);
        } catch (StatusRuntimeException e) {
            log.debug(String.format("Delete note request: {%s} is got with status runtime error: {%s}", noteSearchRequestObject, e));
            throw InternalServerErrorException.builder()
                    .timestamp(ZonedDateTime.now())
                    .debugMessage(e.getMessage())
                    .build();
        }
    }
}
