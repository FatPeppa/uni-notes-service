package org.skyhigh.notesservice.service.tag;

import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.entity.Tag;
import org.skyhigh.notesservice.repository.TagRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagCachedServiceImpl implements TagCachedService {
    private final TagRepository tagRepository;

    @Override
    @Cacheable(value = "TagByIdAndUserIdCache", unless = "#result == null")
    public Tag getTagByIdAndUserId(Long tagId, Long userId) {
        return tagRepository.findByTagIdAndUserId(tagId, userId);
    }

    @Override
    @Cacheable(value = "TagCache", unless = "#result == null")
    public List<Tag> getTagsByUserIdOrderedByCreateDateDescCached(Long userId) {
        return tagRepository.findTagsByUserIdOrderedByCreatedDateDesc(userId);
    }
}
