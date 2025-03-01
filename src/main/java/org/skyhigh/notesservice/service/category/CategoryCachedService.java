package org.skyhigh.notesservice.service.category;

import org.skyhigh.notesservice.data.entity.Category;

import java.util.List;

public interface CategoryCachedService {
    Category getCategoryByIdAndUserId(Long categoryId, Long userId);
    List<Category> getCategoriesByUserIdOrderedByCreateDateDescCached(Long userId);
}
