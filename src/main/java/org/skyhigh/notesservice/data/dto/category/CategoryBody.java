package org.skyhigh.notesservice.data.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело категории", subTypes = ExtendedCategoryBody.class)
public class CategoryBody implements CategoryContent {
    @Schema(description = "ID категории")
    protected Long categoryId;

    @Schema(description = "ID пользователя")
    protected Long userId;

    @Schema(description = "Имя категории")
    protected String name;

    @Schema(description = "Дата создания категории")
    protected ZonedDateTime createdDate;

    @Schema(description = "Дата обновления категории")
    protected ZonedDateTime lastChangedDate;

    @Override
    public int compareTo(CategoryContent o) {
        if (this.createdDate.isBefore(o.getCreatedDate()))
            return -1;
        else if (this.createdDate.isAfter(o.getCreatedDate()))
            return 1;
        else
            return 0;
    }
}
