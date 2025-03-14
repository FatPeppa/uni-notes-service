package org.skyhigh.notesservice.service.note;

import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.note.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface NotesService {
    CreateNoteResponse createNote(CreateNoteRequest createNoteRequest) throws IOException;

    UploadNoteImageResponse uploadNoteImage(Long noteId, MultipartFile image) throws IOException;

    UploadNoteTextResponse uploadNoteText(Long noteId, MultipartFile text) throws IOException;

    GetNotesResponse getNotes(
            Long noteId,
            String noteName,
            Long categoryId,
            List<Long> tagIds,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            boolean extended,
            boolean showImages,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    );

    void updateNoteBody(Long noteId, UpdateNoteBodyRequest updateNoteBodyRequest);

    UpdateNoteTextResponse updateNoteText(Long noteId, MultipartFile text) throws IOException;

    void deleteNote(Long noteId, boolean deleteCascade) throws IOException;

    void deleteImage(Long noteId, UUID mediaId) throws IOException;

    byte[] getImage(Long noteId, UUID mediaId) throws IOException;

    byte[] getText(Long noteId) throws IOException;

    GetNoteMediaMetadataResponse getNoteMediaMetadata(
            Long noteId,
            List<UUID> mediaIds,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    );

    void updateNoteTags(Long noteId, UpdateNoteTagsRequest updateNoteTagsRequest);

    void updateNoteCategory(Long noteId, UpdateNoteCategoryRequest updateNoteCategoryRequest);
}
