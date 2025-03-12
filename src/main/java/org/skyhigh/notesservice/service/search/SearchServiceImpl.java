package org.skyhigh.notesservice.service.search;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.shyhigh.grpc.notes.NoteDataUpdateType;
import org.shyhigh.grpc.notes.ResponseResultCode;
import org.shyhigh.grpc.notes.SearchType;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.note.NoteSearchResponseDetailType;
import org.skyhigh.notesservice.model.dto.note.NoteSearchType;
import org.skyhigh.notesservice.model.dto.search.*;
import org.skyhigh.notesservice.repository.NoteSearchRepository;
import org.skyhigh.notesservice.repository.NoteTagRepository;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.*;
import org.skyhigh.notesservice.validation.flk.Flk10000024;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class SearchServiceImpl implements SearchService {
    private final NoteSearchRepository noteSearchRepository;
    private final UserService userService;
    private final NoteTagRepository noteTagRepository;

    @Override
    public void createNote(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull String noteName,
            Long categoryId,
            String categoryName,
            @NonNull ZonedDateTime createdDate,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        if ((categoryId == null && categoryName != null && !categoryName.isBlank())
                || (categoryId != null && (categoryName == null || categoryName.isBlank())))
            throw InternalServerErrorException.builder()
                    .debugMessage("Grpc Request Preparing Error")
                    .timestamp(ZonedDateTime.now())
                    .build();

        var request = NoteCreateRequestObject.builder()
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name(noteName)
                        .noteCategoryObject(categoryId == null ? null : NoteCategoryObject.builder()
                                .categoryId(categoryId)
                                .categoryName(categoryName)
                                .build())
                        .build())
                .createdDate(createdDate)
                .lastChangeDate(lastChangeDate)
                .build();

        try {
            noteSearchRepository.createNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.CREATE_FAILURE_DATA_ALREADY_EXISTS)
                log.debug(String.format("Note with id:%s already exists in search service", noteId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteNameAndCategory(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull String noteName,
            Long categoryId,
            String categoryName,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        if ((categoryId == null && categoryName != null && !categoryName.isBlank())
                || (categoryId != null && (categoryName == null || categoryName.isBlank())))
            throw InternalServerErrorException.builder()
                    .debugMessage("Grpc Request Preparing Error")
                    .timestamp(ZonedDateTime.now())
                    .build();

        var request = NoteUpdateRequestObject.builder()
                .noteDataUpdateType(NoteDataUpdateType.NOTE_NAME_AND_CATEGORY_UPDATE)
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name(noteName)
                        .noteCategoryObject(categoryId == null ? null : NoteCategoryObject.builder()
                                .categoryId(categoryId)
                                .categoryName(categoryName)
                                .build())
                        .build())
                .lastChangeDate(lastChangeDate)
                .build();

        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteCategoryName(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull Long categoryId,
            @NonNull String categoryName,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        var request = NoteUpdateRequestObject.builder()
                .noteDataUpdateType(NoteDataUpdateType.CATEGORY_NAME_UPDATE)
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name("")
                        .noteCategoryObject(NoteCategoryObject.builder()
                                .categoryId(categoryId)
                                .categoryName(categoryName)
                                .build())
                        .build())
                .lastChangeDate(lastChangeDate)
                .build();

        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteTagName(
            @NonNull Long noteId,
            @NonNull Long userId,
            @NonNull Long tagId,
            @NonNull String tagName,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        var request = NoteUpdateRequestObject.builder()
                .noteDataUpdateType(NoteDataUpdateType.NOTE_TAG_NAME_UPDATE)
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name("")
                        .build())
                .tagId(tagId)
                .tagName(tagName)
                .lastChangeDate(lastChangeDate)
                .build();

        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteTags(
            @NonNull Long noteId,
            @NonNull Long userId,
            List<NoteTagObject> tags,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        var request = NoteUpdateRequestObject.builder()
                .noteDataUpdateType(NoteDataUpdateType.NOTE_TAGS_UPDATE)
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name("")
                        .build())
                .noteTagObjects(tags)
                .lastChangeDate(lastChangeDate)
                .build();
        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteImages(
            @NonNull Long noteId,
            @NonNull Long userId,
            List<UUID> imageIds,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        var request = NoteUpdateRequestObject.builder()
                .noteDataUpdateType(NoteDataUpdateType.NOTE_IMAGES_UPDATE)
                .noteBodyObject(NoteBodyObject.builder()
                        .noteId(noteId)
                        .userId(userId)
                        .name("")
                        .build())
                .imageIds(imageIds)
                .lastChangeDate(lastChangeDate)
                .build();

        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNoteContent(
            @NonNull Long noteId,
            @NonNull Long userId,
            String textExtraction,
            @NonNull UUID mediaId,
            @NonNull MultipartFile content,
            @NonNull ZonedDateTime lastChangeDate
    ) throws RequestException {
        NoteUpdateRequestObject request;
        try {
            request = NoteUpdateRequestObject.builder()
                    .noteDataUpdateType(NoteDataUpdateType.NOTE_CONTENT_UPDATE)
                    .noteBodyObject(NoteBodyObject.builder()
                            .noteId(noteId)
                            .userId(userId)
                            .name("")
                            .build())
                    .mediaId(mediaId)
                    .textExtraction(textExtraction)
                    .content(content.getResource().getContentAsByteArray())
                    .lastChangeDate(lastChangeDate)
                    .build();
        } catch (IOException e) {
            throw InternalServerErrorException.builder()
                    .debugMessage("Grpc Request Preparing Error")
                    .timestamp(ZonedDateTime.now())
                    .build();
        }

        try {
            noteSearchRepository.updateNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST
                    || e.getResponseResultCode() == ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER)
                log.debug(String.format("Note with id:%s not exists or not belong to user with id:%s in search service", noteId, userId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteNote(
            @NonNull Long noteId
    ) throws RequestException {
        var request = NoteDeleteRequestObject.builder()
                .noteId(noteId)
                .build();

        try {
            noteSearchRepository.deleteNote(request);
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.DELETE_FAILURE_DATA_NOT_EXIST)
                log.debug(String.format("Note with id:%s not exists in search service", noteId));
            throw new RuntimeException(e);
        }
    }

    @Override
    public FindNotesResponse findNotes(
            String query,
            NoteSearchType searchType,
            NoteSearchResponseDetailType detailType,
            Long categoryId,
            List<Long> tagIds,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    ) throws RequestException {
        if (limit <= 0 || offset <= 0)
            return FindNotesResponse.builder()
                    .notes(List.of())
                    .build();

        if (beginDate != null && endDate != null && endDate.isBefore(beginDate))
            return FindNotesResponse.builder()
                    .notes(List.of())
                    .build();

        if (tagIds != null && !tagIds.isEmpty() && tagIds.size() > 10)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000024.getCode())
                    .flkMessage(Flk10000024.getMessage())
                    .build()));

        var userId = userService.getCurrentUser().getId();

        var grpcSearchType = switch (searchType) {
            case NORMALIZED -> SearchType.NORMALIZED;
            case NON_NORMALIZED -> SearchType.NON_NORMALIZED;
            case NORMALIZED_FULL_TEXT -> SearchType.NORMALIZED_FULL_TEXT;
            case NON_NORMALIZED_FULL_TEXT -> SearchType.NON_NORMALIZED_FULL_TEXT;
        };
        var grpcDetailType = switch (detailType) {
            case FULL -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.FULL;
            case WITHOUT_CONTENT -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.WITHOUT_CONTENT;
            case WITHOUT_TEXT_EXTRACTION -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.WITHOUT_TEXT_EXTRACTION;
            case WITHOUT_TAGS -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.WITHOUT_TAGS;
            case WITHOUT_MEDIA_IDS -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.WITHOUT_MEDIA_IDS;
            case WITHOUT_TAGS_AND_MEDIA_IDS -> org.shyhigh.grpc.notes.NoteSearchResponseDetailType.WITHOUT_TAGS_AND_MEDIA_IDS;
        };
        var request = NoteSearchRequestObject.builder()
                .searchType(grpcSearchType)
                .userId(userId)
                .query(query)
                .detailType(grpcDetailType)
                .build();
        try {
            var response = noteSearchRepository.searchNotes(
                    request,
                    userId,
                    query
            );
            var noteObjects = response.getResponseResultCode() != ResponseResultCode.SEARCH_SUCCESS
                    ? null
                    : response.getNoteObjects();

            if (noteObjects == null || noteObjects.isEmpty())
                return FindNotesResponse.builder()
                        .notes(List.of())
                        .build();

            noteObjects = noteObjects.stream()
                    .filter(x -> {
                        if (categoryId != null) return
                            x.getNoteCategoryObject() == null
                                    ? null
                                    : x.getNoteCategoryObject().getCategoryId()
                                        .equals(categoryId);
                        else return true;
                    })
                    .filter(x -> {
                        if (tagIds != null && !tagIds.isEmpty()) {
                            if (x.getNoteTagObjects() != null)
                                return new HashSet<>(x.getNoteTagObjects().stream()
                                        .map(NoteTagObject::getTagId).toList()).containsAll(tagIds);
                            else
                                return tagIds.stream().allMatch(y -> noteTagRepository
                                        .findByNoteIdAndTagId(x.getNoteId(), y) != null);
                        }
                        else return true;
                    })
                    .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                    .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                    .toList();

            if (createdDateSortDirection == SortDirection.ASC)
                noteObjects = noteObjects.stream().sorted().toList();
            if (createdDateSortDirection == SortDirection.DESC) {
                var noteObjectsArrayList = new ArrayList<>(noteObjects);
                noteObjectsArrayList.sort(Comparator.reverseOrder());
                noteObjects = noteObjectsArrayList;
            }

            return FindNotesResponse.builder()
                    .notes(noteObjects)
                    .build();
        } catch (GrpcResponseException e) {
            if (e.getResponseResultCode() == ResponseResultCode.SEARCH_NOTES_NOT_FOUND)
                return FindNotesResponse.builder()
                        .notes(List.of())
                        .build();
            throw new RuntimeException(e);
        }
    }
}
