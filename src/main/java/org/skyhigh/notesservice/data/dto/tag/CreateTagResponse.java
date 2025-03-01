package org.skyhigh.notesservice.data.dto.tag;

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
@Schema(description = "Тело ответа на создание тега")
public class CreateTagResponse {
    @Schema(description = "ID тега")
    private Long id;

    @Schema(description = "Имя тега")
    private String name;

    @Schema(description = "Дата и время создания тега")
    private ZonedDateTime createdDate;
}
