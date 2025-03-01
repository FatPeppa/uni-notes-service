package org.skyhigh.notesservice.service.category;

import org.skyhigh.notesservice.data.dto.category.CreateCategoryRequest;
import org.skyhigh.notesservice.data.dto.category.CreateCategoryResponse;
import org.skyhigh.notesservice.data.dto.category.GetCategoriesResponse;
import org.skyhigh.notesservice.data.dto.category.UpdateCategoryRequest;
import org.skyhigh.notesservice.data.dto.common.SortDirection;

import java.time.ZonedDateTime;

public interface CategoryService {
    CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);
    void updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest);
    void deleteCategory(Long categoryId);
    GetCategoriesResponse getCategories(
            Long categoryId,
            String categoryName,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            boolean extended,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    );
}
