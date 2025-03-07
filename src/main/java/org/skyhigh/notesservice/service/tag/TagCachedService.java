package org.skyhigh.notesservice.service.tag;

import org.skyhigh.notesservice.model.entity.Tag;

import java.util.List;

public interface TagCachedService {
    Tag getTagByIdAndUserId(Long tagId, Long userId);
    List<Tag> getTagsByUserIdOrderedByCreateDateDescCached(Long userId);
}
