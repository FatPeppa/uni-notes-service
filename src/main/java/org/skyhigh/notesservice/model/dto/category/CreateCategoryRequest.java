package org.skyhigh.notesservice.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело запроса на создание категории заметок")
public class CreateCategoryRequest {
    @Schema(description = "Имя категории; до 255 символов")
    @NotEmpty
    @Size(max = "255")
    private String name;

    @Schema(description = "Описание категории; до 512 символов")
    @Size(max = "512")
    private String description;
}
