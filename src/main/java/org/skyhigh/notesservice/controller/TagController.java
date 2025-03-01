package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.dto.common.SortDirection;
import org.skyhigh.notesservice.data.dto.tag.CreateTagRequest;
import org.skyhigh.notesservice.data.dto.tag.CreateTagResponse;
import org.skyhigh.notesservice.data.dto.tag.GetTagsResponse;
import org.skyhigh.notesservice.data.dto.tag.UpdateTagRequest;
import org.skyhigh.notesservice.service.tag.TagService;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/tags/v1")
@RequiredArgsConstructor
@Tag(name = "Теги")
public class TagController {
    private final TagService tagService;

    @Operation(summary = "Создать тег")
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ValidParams
    public ResponseEntity<CreateTagResponse> createCategory(
            @RequestBody CreateTagRequest createTagRequest
    ) {
        return ResponseEntity.ok(
                tagService.createTag(createTagRequest)
        );
    }

    @Operation(summary = "Обновить тег")
    @PutMapping(value = "/{tagId}", consumes = "application/json")
    @ValidParams
    public ResponseEntity<Void> updateTag(
            @PathVariable("tagId") @Parameter(description = "ID тега") Long tagId,
            @RequestBody UpdateTagRequest updateTagRequest
    ) {
        tagService.updateTag(tagId, updateTagRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить тег")
    @DeleteMapping(value = "/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable("tagId") @Parameter(description = "ID тега") Long tagId
    ) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить свои теги")
    @GetMapping(produces = "application/json")
    public ResponseEntity<GetTagsResponse> getTags(
            @RequestParam(required = false) @Parameter(description = "ID тега") Long tagId,
            @RequestParam(required = false) @Parameter(description = "Имя тега") String tagName,
            @RequestParam(required = false) @Parameter(description = "Дата и время начала диапазона поиска") ZonedDateTime beginDate,
            @RequestParam(required = false) @Parameter(description = "Дата и время окончания диапазона поиска") ZonedDateTime endDate,
            @RequestParam(required = false, defaultValue = "DESC") @Parameter(description = "Направление сортировки по дате создания") SortDirection createdDateSortDirection,
            @RequestParam(required = false, defaultValue = "50") @Parameter(description = "Количество записей в ответе") Integer limit,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Смещение выдаваемой выборки") Integer offset
    ) {
        return ResponseEntity.ok(tagService.getTags(
                tagId,
                tagName,
                beginDate,
                endDate,
                createdDateSortDirection,
                limit,
                offset
        ));
    }
}
