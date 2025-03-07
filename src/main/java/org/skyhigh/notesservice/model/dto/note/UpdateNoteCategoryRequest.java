package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело запроса по обновлению категории заметки")
public class UpdateNoteCategoryRequest {
    @Schema(description = "Id категории")
    private Long categoryId;
}
