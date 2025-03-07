package org.skyhigh.notesservice.model.dto.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа по созданию заметки")
public class CreateNoteResponse {
    @Schema(description = "ID заметки")
    private Long noteId;

    @Schema(description = "Заголовок заметки")
    private String name;

    @Schema(description = "Дата и время создания")
    private ZonedDateTime createdDate;
}
