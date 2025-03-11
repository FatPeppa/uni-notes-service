package org.skyhigh.notesservice.repository;

import org.skyhigh.notesservice.model.dto.search.*;
import org.skyhigh.notesservice.validation.exception.GrpcResponseException;
import org.skyhigh.notesservice.validation.exception.RequestException;

public interface NoteSearchRepository {
    void createNote(NoteCreateRequestObject noteCreateRequestObject) throws RequestException, GrpcResponseException;
    void updateNote(NoteUpdateRequestObject noteUpdateRequestObject) throws RequestException, GrpcResponseException;
    void deleteNote(NoteDeleteRequestObject noteDeleteRequestObject) throws RequestException, GrpcResponseException;
    NoteSearchResponseObject searchNotes(
            NoteSearchRequestObject noteSearchRequestObject,
            Long userId,
            String query
    ) throws RequestException, GrpcResponseException;
}
