package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.model.dto.category.CreateCategoryRequest;
import org.skyhigh.notesservice.model.dto.category.CreateCategoryResponse;
import org.skyhigh.notesservice.model.dto.category.GetCategoriesResponse;
import org.skyhigh.notesservice.model.dto.category.UpdateCategoryRequest;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.service.category.CategoryService;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/categories/v1")
@RequiredArgsConstructor
@Tag(name = "Категории")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Создать категорию")
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ValidParams
    public ResponseEntity<CreateCategoryResponse> createCategory(
            @RequestBody CreateCategoryRequest createCategoryRequest
    ) {
        return ResponseEntity.ok(
                categoryService.createCategory(createCategoryRequest)
        );
    }

    @Operation(summary = "Обновить категорию")
    @PutMapping(value = "/{categoryId}", consumes = "application/json")
    @ValidParams
    public ResponseEntity<Void> updateCategory(
            @PathVariable("categoryId") @Parameter(description = "ID категории") Long categoryId,
            @RequestBody UpdateCategoryRequest updateCategoryRequest
    ) {
        categoryService.updateCategory(categoryId, updateCategoryRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить категорию")
    @DeleteMapping(value = "/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("categoryId") @Parameter(description = "ID категории") Long categoryId
    ) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить свои категории")
    @GetMapping(produces = "application/json")
    public ResponseEntity<GetCategoriesResponse> getCategories(
            @RequestParam(required = false) @Parameter(description = "ID категории") Long categoryId,
            @RequestParam(required = false) @Parameter(description = "Имя категории") String categoryName,
            @RequestParam(required = false) @Parameter(description = "Дата и время начала диапазона поиска") ZonedDateTime beginDate,
            @RequestParam(required = false) @Parameter(description = "Дата и время окончания диапазона поиска") ZonedDateTime endDate,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Признак отображения расширенных сведений") boolean extended,
            @RequestParam(required = false, defaultValue = "DESC") @Parameter(description = "Направление сортировки по дате создания") SortDirection createdDateSortDirection,
            @RequestParam(required = false, defaultValue = "25") @Parameter(description = "Количество записей в ответе") Integer limit,
            @RequestParam(required = false, defaultValue = "1") @Parameter(description = "Смещение выдаваемой выборки") Integer offset
    ) {
        return ResponseEntity.ok(categoryService.getCategories(
                categoryId,
                categoryName,
                beginDate,
                endDate,
                extended,
                createdDateSortDirection,
                limit,
                offset
        ));
    }
}
