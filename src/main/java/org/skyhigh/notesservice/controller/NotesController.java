package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.dto.common.SortDirection;
import org.skyhigh.notesservice.data.dto.note.*;
import org.skyhigh.notesservice.service.note.NotesService;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/notes/v1")
@RequiredArgsConstructor
@Tag(name = "Заметки")
public class NotesController {
    private final NotesService notesService;

    @Operation(summary = "Создать заметку")
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ValidParams
    public ResponseEntity<CreateNoteResponse> createNote(
            @RequestBody @Parameter(description = "Тело запроса на создание заметки") CreateNoteRequest createNoteRequest
    ) throws IOException {
        return ResponseEntity.ok(notesService.createNote(createNoteRequest));
    }

    @Operation(summary = "Загрузить текст к заметке")
    @PostMapping(value = "/{noteId}/resources/text", consumes = MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<UploadNoteTextResponse> uploadNoteText(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestParam @Parameter(description = "Файл с текстом заметки") MultipartFile text
    ) throws IOException {
        return ResponseEntity.ok(notesService.uploadNoteText(noteId, text));
    }

    @Operation(summary = "Получить свои заметки")
    @GetMapping(produces = "application/json")
    public ResponseEntity<GetNotesResponse> getNotes(
            @RequestParam(required = false) @Parameter(description = "ID заметки") Long noteId,
            @RequestParam(required = false) @Parameter(description = "Имя заметки") String noteName,
            @RequestParam(required = false) @Parameter(description = "ID категории заметки") Long categoryId,
            @RequestParam(required = false) @Parameter(description = "ID тегов заметки") List<Long> tagIds,
            @RequestParam(required = false) @Parameter(description = "Дата и время начала диапазона поиска") ZonedDateTime beginDate,
            @RequestParam(required = false) @Parameter(description = "Дата и время окончания диапазона поиска") ZonedDateTime endDate,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Признак отображения расширенных сведений") boolean extended,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Признак отображения ID фото заметок") boolean showImages,
            @RequestParam(required = false) @Parameter(description = "Направление сортировки по дате создания") SortDirection createdDateSortDirection,
            @RequestParam(required = false, defaultValue = "50") @Parameter(description = "Количество записей в ответе") Integer limit,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Смещение выдаваемой выборки") Integer offset
    ) {
        return ResponseEntity.ok(notesService.getNotes(
                noteId,
                noteName,
                categoryId,
                tagIds,
                beginDate,
                endDate,
                extended,
                showImages,
                createdDateSortDirection,
                limit,
                offset
        ));
    }

    @Operation(summary = "Обновить тело заметки")
    @PutMapping(value = "/{noteId}", consumes = "application/json")
    @ValidParams
    public ResponseEntity<Void> updateNoteBody(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestBody UpdateNoteBodyRequest updateNoteBodyRequest
    ) {
        notesService.updateNoteBody(noteId, updateNoteBodyRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить текст заметки")
    @PutMapping(value = "/{noteId}/resources/text", consumes = MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<UpdateNoteTextResponse> updateNoteText(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestParam @Parameter(description = "Файл с текстом заметки") MultipartFile text
    ) throws IOException {
        return ResponseEntity.ok(notesService.updateNoteText(noteId, text));
    }

    @Operation(summary = "Удалить заметку")
    @DeleteMapping(value = "/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Признак каскадного удаления") boolean deleteCascade
    ) throws IOException {
        notesService.deleteNote(noteId, deleteCascade);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить фото из заметки")
    @DeleteMapping(value = "/{noteId}/resources/images/{mediaId}")
    public ResponseEntity<Void> deleteNoteImages(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @PathVariable("mediaId") @Parameter(description = "ID фото") UUID mediaId
    ) throws IOException {
        notesService.deleteImage(noteId, mediaId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить фото заметки")
    @GetMapping(value = "/{noteId}/resources/images/{mediaId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadNoteImage(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @PathVariable("mediaId") @Parameter(description = "ID фото") UUID mediaId
    ) throws IOException {
        return ResponseEntity.ok(notesService.getImage(noteId, mediaId));
    }

    @Operation(summary = "Получить текст заметки")
    @GetMapping(value = "/{noteId}/resources/text", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadNoteText(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId
    ) throws IOException {
        return ResponseEntity.ok(notesService.getText(noteId));
    }

    @Operation(summary = "Обновить теги заметки")
    @PutMapping(value = "/{noteId}/tags", consumes = "application/json")
    public ResponseEntity<Void> updateNoteTags(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestBody @Parameter(description = "Тело запроса на обновление тегов заметки") UpdateNoteTagsRequest updateNoteTagsRequest
    ) {
        notesService.updateNoteTags(noteId, updateNoteTagsRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить категорию заметки")
    @PutMapping(value = "/{noteId}/category", consumes = "application/json")
    public ResponseEntity<Void> updateNoteCategory(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestBody @Parameter(description = "Тело запроса на обновление категории заметки") UpdateNoteCategoryRequest updateNoteCategoryRequest
    ) {
        notesService.updateNoteCategory(noteId, updateNoteCategoryRequest);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Загрузить фото к заметке")
    @PutMapping(value = "/{noteId}/resources/images", consumes = MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<UploadNoteImageResponse> uploadNoteImage(
            @PathVariable("noteId") @Parameter(description = "ID заметки") Long noteId,
            @RequestParam @Parameter(description = "Файл с фото, прикрепляемый к заметке") MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok(notesService.uploadNoteImage(noteId, image));
    }
}
