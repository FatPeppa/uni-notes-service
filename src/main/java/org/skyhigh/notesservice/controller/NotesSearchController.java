package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.note.NoteSearchResponseDetailType;
import org.skyhigh.notesservice.model.dto.note.NoteSearchType;
import org.skyhigh.notesservice.model.dto.search.FindNotesResponse;
import org.skyhigh.notesservice.service.search.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/search/v1/notes")
@RequiredArgsConstructor
@Tag(name = "Поиск заметок")
public class NotesSearchController {
    private final SearchService searchService;

    @Operation(summary = "Получить метаданные фото заметки")
    @GetMapping(produces = "application/json")
    public ResponseEntity<FindNotesResponse> findNotes(
            @RequestParam(required = false, defaultValue = "") @Parameter(description = "Текстовый запрос (строка, введенная пользователем для поиска)") String query,
            @RequestParam(required = false, defaultValue = "NORMALIZED_FULL_TEXT") @Parameter(description = "Тип поиска заметок") NoteSearchType searchType,
            @RequestParam(required = false, defaultValue = "FULL") @Parameter(description = "Тип детализации поиска заметок") NoteSearchResponseDetailType detailType,
            @RequestParam(required = false) @Parameter(description = "Дата и время начала диапазона поиска") ZonedDateTime beginDate,
            @RequestParam(required = false) @Parameter(description = "Дата и время окончания диапазона поиска") ZonedDateTime endDate,
            @RequestParam(required = false) @Parameter(description = "Направление сортировки по дате создания") SortDirection createdDateSortDirection,
            @RequestParam(required = false, defaultValue = "50") @Parameter(description = "Количество записей в ответе") Integer limit,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Смещение выдаваемой выборки") Integer offset
    ) {
        return ResponseEntity.ok(searchService.findNotes(
                query,
                searchType,
                detailType,
                beginDate,
                endDate,
                createdDateSortDirection,
                limit,
                offset
        ));
    }
}
