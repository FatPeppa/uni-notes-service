package org.skyhigh.notesservice.service.search;

import lombok.NonNull;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.note.NoteSearchResponseDetailType;
import org.skyhigh.notesservice.model.dto.note.NoteSearchType;
import org.skyhigh.notesservice.model.dto.search.FindNotesResponse;
import org.skyhigh.notesservice.model.dto.search.NoteTagObject;
import org.skyhigh.notesservice.validation.exception.RequestException;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface SearchService {
    void createNote(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull String noteName,
            Long categoryId,
            String categoryName,
            @NonNull ZonedDateTime createdDate,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void updateNoteNameAndCategory(
        @NonNull Long noteId,
        @NonNull Long userId,
        @NonNull String noteName,
        Long categoryId,
        String categoryName,
        @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void updateNoteCategoryName(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull Long categoryId,
            @NonNull String categoryName,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void updateNoteTagName(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull Long tagId,
            @NonNull String tagName,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void updateNoteTags(
            @NonNull Long noteId,
            @NonNull Long userId,
            List<NoteTagObject> tags,
            @NonNull ZonedDateTime lastChangeDate
    )throws RequestException;

    void updateNoteImages(
            @NonNull Long noteId,
            @NonNull Long userId,
            List<UUID> imageIds,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void updateNoteContent(
            @NonNull Long noteId,
            @NonNull Long userId,
            String textExtraction,
            @NonNull UUID mediaId,
            @NonNull MultipartFile content,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException;

    void deleteNote(
            @NonNull Long noteId
    ) throws RequestException;

    FindNotesResponse findNotes(
            String query,
            NoteSearchType searchType,
            NoteSearchResponseDetailType detailType,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    ) throws RequestException;
}
