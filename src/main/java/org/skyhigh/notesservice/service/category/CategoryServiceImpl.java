package org.skyhigh.notesservice.service.category;

import org.skyhigh.notesservice.common.Paginator;
import org.skyhigh.notesservice.data.dto.category.*;
import org.skyhigh.notesservice.data.dto.common.SortDirection;
import org.skyhigh.notesservice.data.entity.Category;
import org.skyhigh.notesservice.repository.CategoryRepository;
import org.skyhigh.notesservice.repository.NoteRepository;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;
import org.skyhigh.notesservice.validation.flk.Flk10000009;
import org.skyhigh.notesservice.validation.flk.Flk10000018;
import org.skyhigh.notesservice.validation.flk.Flk10000019;
import org.skyhigh.notesservice.validation.flk.Flk10000020;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryCachedService categoryCachedService;
    private final UserService userService;

    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;

    private final Integer maxUsersCategoriesAmount;

    public CategoryServiceImpl(
            CategoryCachedService categoryCachedService,
            UserService userService,
            CategoryRepository categoryRepository,
            NoteRepository noteRepository,
            @Qualifier("MaxUsersCategoriesAmount") Integer maxUsersCategoriesAmount
    ) {
        this.categoryCachedService = categoryCachedService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.noteRepository = noteRepository;
        this.maxUsersCategoriesAmount = maxUsersCategoriesAmount;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"CategoryCache", "CategoryByIdAndUserIdCache"}, allEntries = true)
    public CreateCategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить количество категорий у пользователя
        if (categoryRepository.countUserCategories(userId) >= maxUsersCategoriesAmount)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000020.getCode())
                    .flkMessage(Flk10000020.getMessage())
                    .build()));

        //2. Проверить существование категории с указанным именем
        if (categoryRepository.findByNameAndUserId(createCategoryRequest.getName(), userId) != null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000018.getCode())
                    .flkMessage(Flk10000018.getMessage())
                    .flkParameterName(Flk10000018.getFieldName())
                    .build()));

        //3. Создать категорию
        var createdDate = ZonedDateTime.now();

        Category category = Category.builder()
                .userId(userId)
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .createdDate(createdDate)
                .lastChangeDate(createdDate)
                .build();

        category = categoryRepository.save(category);

        return CreateCategoryResponse.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .createdDate(createdDate)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"CategoryCache", "CategoryByIdAndUserIdCache"}, allEntries = true)
    public void updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить существование категории с указанным Id
        var category = categoryCachedService.getCategoryByIdAndUserId(categoryId, userId);
        if (category == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000009.getCode())
                    .flkMessage(Flk10000009.getMessage())
                    .build()));

        //2. Проверить целевое имя категории на предмет наличия иных категорий с таким именем
        var categoryWithSameName = categoryRepository.findByNameAndUserId(category.getName(), userId);
        if (categoryWithSameName != null && !Objects.equals(categoryId, categoryWithSameName.getId()))
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000018.getCode())
                    .flkMessage(Flk10000018.getMessage())
                    .build()));

        //3. Обновить категорию
        categoryRepository.updateCategoryNameAndDescriptionAndLastChangeDateById(
                categoryId,
                updateCategoryRequest.getName(),
                updateCategoryRequest.getDescription(),
                ZonedDateTime.now()
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = {"CategoryCache", "CategoryByIdAndUserIdCache"}, allEntries = true)
    public void deleteCategory(Long categoryId) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить существование категории с указанным Id
        var category = categoryCachedService.getCategoryByIdAndUserId(categoryId, userId);
        if (category == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000009.getCode())
                    .flkMessage(Flk10000009.getMessage())
                    .build()));

        //2. Проверить наличие заметок с указанной категорией
        var notes = noteRepository.findByUserIdAndCategoryId(userId, categoryId);
        if (notes != null && !notes.isEmpty())
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000019.getCode())
                    .flkMessage(Flk10000019.getMessage())
                    .build()));

        //3. Удалить категорию
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public GetCategoriesResponse getCategories(
            Long categoryId,
            String categoryName,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            boolean extended,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    ) {
        if (limit <= 0 || offset <= 0)
            return GetCategoriesResponse.builder()
                    .categories(new ArrayList<>())
                    .build();

        if (beginDate != null && endDate != null && endDate.isBefore(beginDate))
            return GetCategoriesResponse.builder()
                    .categories(new ArrayList<>())
                    .build();

        var userId = userService.getCurrentUser().getId();

        //1. Поиск категорий по дате в обратном порядке (от настоящего к прошлому)
        var categories = categoryCachedService.getCategoriesByUserIdOrderedByCreateDateDescCached(userId);

        if (categories == null)
            categories = new ArrayList<>();

        List<CategoryContent> categoryContents = null;

        //2. Преобразование списка категорий к расширенному/упрощенному представлению и фильтрация
        if (extended) {
            //2.1 Маппинг к ExtendedCategoryBody
            List<CategoryContent> finalCategoryContents = new ArrayList<>();
            categories.forEach(x -> {
                finalCategoryContents.add(new ExtendedCategoryBody(
                        x.getId(),
                        x.getUserId(),
                        x.getName(),
                        x.getDescription(),
                        x.getCreatedDate(),
                        x.getLastChangeDate()
                ));
            });

            //2.2 Фильтрация
            categoryContents = new ArrayList<>(finalCategoryContents).stream()
                    .filter(x -> {if (categoryId != null) return ((ExtendedCategoryBody) x).getId().equals(categoryId); else return true;})
                    .filter(x -> {if (categoryName != null && !categoryName.isBlank()) return ((ExtendedCategoryBody) x).getName().equals(categoryName); else return true;})
                    .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                    .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                    .toList();
        } else {
            //2.1 Маппинг к CategoryBody
            List<CategoryContent> finalCategoryContents = new ArrayList<>();
            categories.forEach(x -> {
                finalCategoryContents.add(new CategoryBody(
                        x.getId(),
                        x.getUserId(),
                        x.getName(),
                        x.getCreatedDate(),
                        x.getLastChangeDate()
                ));
            });

            //2.2 Фильтрация
            categoryContents = new ArrayList<>(finalCategoryContents).stream()
                    .filter(x -> {if (categoryId != null) return ((CategoryBody) x).getId().equals(categoryId); else return true;})
                    .filter(x -> {if (categoryName != null && !categoryName.isBlank()) return ((CategoryBody) x).getName().equals(categoryName); else return true;})
                    .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                    .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                    .toList();
        }

        //3. Сортировка в прямом порядке в случае необходимости
        if (createdDateSortDirection == SortDirection.ASC)
            categoryContents = categoryContents.stream().sorted().toList();

        categoryContents = Paginator.paginate(
                categoryContents,
                offset,
                limit
        );

        return GetCategoriesResponse.builder()
                .categories(categoryContents)
                .build();
    }
}
