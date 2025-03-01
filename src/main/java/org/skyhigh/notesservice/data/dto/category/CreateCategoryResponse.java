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
@Schema(description = "Тело ответа на создание категории заметок")
public class CreateCategoryResponse {
    private Long categoryId;
    private String name;

    @Schema(description = "Дата и время создания")
    private ZonedDateTime createdDate;
}
