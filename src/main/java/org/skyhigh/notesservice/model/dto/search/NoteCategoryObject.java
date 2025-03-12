package org.skyhigh.notesservice.model.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Категория заметки")
public class NoteCategoryObject {
    @Schema(description = "ID категории")
    private Long categoryId;

    @Schema(description = "Имя категории")
    private String categoryName;
}
