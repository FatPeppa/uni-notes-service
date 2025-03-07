package org.skyhigh.notesservice.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Расширенное тело категории")
public class ExtendedCategoryBody extends CategoryBody implements CategoryContent {
    @Schema(description = "Описание категории")
    private String description;

    public ExtendedCategoryBody(
            Long id,
            Long userId,
            String name,
            String description,
            ZonedDateTime createdDate,
            ZonedDateTime lastChangedDate
    ) {
        super(
                id,
                userId,
                name,
                createdDate,
                lastChangedDate
        );
        this.description = description;
    }

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
