package org.skyhigh.notesservice.service.tag;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.common.Paginator;
import org.skyhigh.notesservice.model.dto.common.SortDirection;
import org.skyhigh.notesservice.model.dto.search.NoteTagObject;
import org.skyhigh.notesservice.model.dto.tag.*;
import org.skyhigh.notesservice.model.entity.Tag;
import org.skyhigh.notesservice.repository.NoteRepository;
import org.skyhigh.notesservice.repository.NoteTagRepository;
import org.skyhigh.notesservice.repository.TagRepository;
import org.skyhigh.notesservice.service.search.SearchService;
import org.skyhigh.notesservice.service.user.UserService;
import org.skyhigh.notesservice.validation.exception.FlkException;
import org.skyhigh.notesservice.validation.exception.MultipleFlkException;
import org.skyhigh.notesservice.validation.flk.Flk10000017;
import org.skyhigh.notesservice.validation.flk.Flk10000021;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagCachedService tagCachedService;
    private final UserService userService;
    private final SearchService searchService;

    private final TagRepository tagRepository;

    private final NoteTagRepository noteTagRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"TagCache", "TagByIdAndUserIdCache"}, allEntries = true)
    public CreateTagResponse createTag(CreateTagRequest createTagRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить существование тега с указанным именем
        if (tagRepository.findByNameAndUserId(createTagRequest.getName(), userId) != null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000021.getCode())
                    .flkMessage(Flk10000021.getMessage())
                    .flkParameterName(Flk10000021.getFieldName())
                    .build()));

        //2. Создать тег
        var createdDate = ZonedDateTime.now();

        var tag = Tag.builder()
                .userId(userId)
                .name(createTagRequest.getName())
                .createdDate(createdDate)
                .lastChangeDate(createdDate)
                .build();

        tag = tagRepository.save(tag);

        return CreateTagResponse.builder()
                .tagId(tag.getId())
                .name(createTagRequest.getName())
                .createdDate(createdDate)
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"TagCache", "TagByIdAndUserIdCache"}, allEntries = true)
    public void updateTag(Long tagId, UpdateTagRequest updateTagRequest) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить существование тега с указанным Id
        var tag = tagCachedService.getTagByIdAndUserId(tagId, userId);
        if (tag == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000017.getCode())
                    .flkMessage(Flk10000017.getMessage())
                    .build()));

        //2. Проверить целевое имя тега на предмет наличия иных тегов с таким именем
        var tagWithSameName = tagRepository.findByNameAndUserId(tag.getName(), userId);
        if (tagWithSameName != null && !Objects.equals(tagId, tagWithSameName.getId()))
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000021.getCode())
                    .flkMessage(Flk10000021.getMessage())
                    .flkParameterName(Flk10000021.getFieldName())
                    .build()));

        //3. Обновить тег
        var lastChangeDate = ZonedDateTime.now();
        tagRepository.updateTagNameAndLastChangeDateById(
                tagId,
                updateTagRequest.getName(),
                lastChangeDate
        );

        var notes = noteRepository.findByUserIdAndTagId(
                userId,
                tagId
        );

        if (notes != null && !notes.isEmpty())
            notes.forEach(x -> searchService.updateNoteTagName(
                    x.getId(),
                    userId,
                    tagId,
                    updateTagRequest.getName(),
                    lastChangeDate
            ));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = {"TagCache", "TagByIdAndUserIdCache"}, allEntries = true)
    public void deleteTag(Long tagId) {
        var userId = userService.getCurrentUser().getId();

        //1. Проверить существование тега с указанным Id
        var tag = tagCachedService.getTagByIdAndUserId(tagId, userId);
        if (tag == null)
            throw new MultipleFlkException(List.of(FlkException.builder()
                    .flkCode(Flk10000017.getCode())
                    .flkMessage(Flk10000017.getMessage())
                    .build()));

        //2. Удалить тег
        tagRepository.deleteById(tagId);


        var notes = noteRepository.findByUserIdAndTagId(userId, tagId);
        if (notes != null && !notes.isEmpty())
            notes.forEach(x -> {
                var noteTags = tagRepository.findByNoteId(x.getId());
                noteTags = noteTags != null && !noteTags.isEmpty()
                        ? noteTags.stream().filter(y -> y.getId().equals(tagId)).toList()
                        : null;
                searchService.updateNoteTags(
                    x.getId(),
                    userId,
                    noteTags == null
                            ? null
                            : noteTags.stream().map(y -> new NoteTagObject(
                                    y.getId(),
                                    y.getName()
                            )).toList(),
                    ZonedDateTime.now()
                );
            });
    }

    @Override
    public GetTagsResponse getTags(Long tagId, String tagName, ZonedDateTime beginDate, ZonedDateTime endDate, SortDirection createdDateSortDirection, Integer limit, Integer offset) {
        if (limit <= 0 || offset <= 0)
            return GetTagsResponse.builder()
                    .tags(new ArrayList<>())
                    .build();

        if (beginDate != null && endDate != null && endDate.isBefore(beginDate))
            return GetTagsResponse.builder()
                    .tags(new ArrayList<>())
                    .build();

        var userId = userService.getCurrentUser().getId();

        //1. Поиск тегов по дате в обратном порядке (от настоящего к прошлому)
        var tags = tagCachedService.getTagsByUserIdOrderedByCreateDateDescCached(userId);

        if (tags == null)
            return GetTagsResponse.builder()
                    .tags(new ArrayList<>())
                    .build();

        //2. Преобразование списка тегов к dto и фильтрация
        List<FullTagBody> fullTagBodies = null;

        //2.1 Маппинг к FullTagBody
        List<FullTagBody> finalFullTagBodies = new ArrayList<>();
        tags.forEach(x -> {
            finalFullTagBodies.add(new FullTagBody(
                    x.getId(),
                    x.getUserId(),
                    x.getName(),
                    x.getCreatedDate(),
                    x.getLastChangeDate()
            ));
        });

        //2.2 Фильтрация
        fullTagBodies = new ArrayList<>(finalFullTagBodies).stream()
                .filter(x -> {if (tagId != null) return x.getTagId().equals(tagId); else return true;})
                .filter(x -> {if (tagName != null && !tagName.isBlank()) return x.getName().contains(tagName); else return true;})
                .filter(x -> {if (beginDate != null) return x.getCreatedDate().isAfter(beginDate) || x.getCreatedDate().isEqual(beginDate); else return true;})
                .filter(x -> {if (endDate != null) return x.getCreatedDate().isBefore(endDate) || x.getCreatedDate().isEqual(endDate); else return true;})
                .toList();

        //3. Сортировка в прямом порядке в случае необходимости
        if (createdDateSortDirection == SortDirection.ASC)
            fullTagBodies = fullTagBodies.stream().sorted().toList();

        fullTagBodies = Paginator.paginate(
                fullTagBodies,
                offset,
                limit
        );

        return GetTagsResponse.builder()
                .tags(fullTagBodies)
                .build();
    }
}