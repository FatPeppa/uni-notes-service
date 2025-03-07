package org.skyhigh.notesservice.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Schema(
        description = "Содержимое категории",
        subTypes = {CategoryBody.class, ExtendedCategoryBody.class}
)
public interface CategoryContent extends Serializable, Comparable<CategoryContent> {
    ZonedDateTime getCreatedDate();
}
