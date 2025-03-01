package org.skyhigh.notesservice.service.category;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.entity.Category;
import org.skyhigh.notesservice.repository.CategoryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCachedServiceImpl implements CategoryCachedService {
    private final CategoryRepository categoryRepository;

    @Override
    @Cacheable(value = "CategoryByIdAndUserIdCache", unless = "#result == null")
    public Category getCategoryByIdAndUserId(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId);
    }

    @Override
    @Cacheable(value = "CategoryCache", unless = "#result == null")
    public List<Category> getCategoriesByUserIdOrderedByCreateDateDescCached(Long userId) {
        return categoryRepository.findCategoriesByUserIdOrderedByCreatedDateDesc(userId);
    }
}
