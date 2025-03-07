package org.skyhigh.notesservice.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа по получению категорий")
public class GetCategoriesResponse {
    @Schema(description = "Список категорий")
    private List<CategoryContent> categories;
}
