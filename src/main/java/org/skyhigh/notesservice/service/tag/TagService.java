package org.skyhigh.notesservice.service.tag;

import org.skyhigh.notesservice.data.dto.common.SortDirection;
import org.skyhigh.notesservice.data.dto.tag.CreateTagRequest;
import org.skyhigh.notesservice.data.dto.tag.CreateTagResponse;
import org.skyhigh.notesservice.data.dto.tag.GetTagsResponse;
import org.skyhigh.notesservice.data.dto.tag.UpdateTagRequest;

import java.time.ZonedDateTime;

public interface TagService {
    CreateTagResponse createTag(CreateTagRequest createTagRequest);
    void updateTag(Long tagId, UpdateTagRequest updateTagRequest);
    void deleteTag(Long tagId);
    GetTagsResponse getTags(
            Long tagId,
            String tagName,
            ZonedDateTime beginDate,
            ZonedDateTime endDate,
            SortDirection createdDateSortDirection,
            Integer limit,
            Integer offset
    );
}
